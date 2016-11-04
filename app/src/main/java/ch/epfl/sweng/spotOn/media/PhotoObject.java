package ch.epfl.sweng.spotOn.media;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.user.UserId;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

/**
 *  This class represents a picture and all associated information : name, thumbnail, pictureID, author, timestamps, position, radius
 *  It is mean to be used locally, as opposed to the PhotoObjectStoredInDatabase which is used to be stored in the database
 */
public class PhotoObject {

    private final long DEFAULT_PICTURE_LIFETIME = 24*60*60*1000; // in milliseconds - 24H
    private final int THUMBNAIL_SIZE = 128; // in pixels

    public final static int MAX_VIEW_RADIUS = 7000;    // in meters
    public final static int DEFAULT_VIEW_RADIUS = 70;
    public final static int MIN_VIEW_RADIUS = 20;

    private Bitmap mFullsizeImage;
    private String mFullsizeImageLink;   // needed for the "cache-like" behaviour of getFullsizeImage()
    private boolean mHasFullsizeImage;   // false -> no Image, but has link; true -> has image, can't access link (don't need it => no getter)
    private Bitmap mThumbnail;
    private String mPictureId;
    private String mAuthorID;
    private String mPhotoName;
    private Timestamp mCreatedDate;
    private Timestamp mExpireDate;
    private double mLatitude;
    private double mLongitude;
    private int mRadius;
    private boolean mStoredInternally;
    private int mNbUpvotes;
    private int mNbDownvotes;
    private ArrayList<String> mDownvotersList;
    private ArrayList<String> mUpvotersList;


    /** This constructor will be used when the user takes a photo with his device, and create the object from locally obtained information
     *  pictureId should be created by calling .push().getKey() on the DatabaseReference where the object should be stored */
    public PhotoObject(Bitmap fullSizePic, String authorID, String photoName,
                       Timestamp createdDate, double latitude, double longitude){
        mFullsizeImage = fullSizePic.copy(fullSizePic.getConfig(), true);
        mHasFullsizeImage=true;
        mFullsizeImageLink = null;  // link not avaiable yet
        mThumbnail = createThumbnail(mFullsizeImage);
        mPictureId = DatabaseRef.getMediaDirectory().push().getKey();   //available even offline
        mPhotoName = photoName;
        mCreatedDate = createdDate;
        mExpireDate = new Timestamp(createdDate.getTime()+DEFAULT_PICTURE_LIFETIME);
        mLatitude = latitude;
        mLongitude = longitude;
        mAuthorID = authorID;
        mStoredInternally = false;
        mNbUpvotes = 1;     // initialize at 1 to avoid any possible division by 0 later
        mNbDownvotes = 1;
        mRadius = computeRadius();
        mDownvotersList = new ArrayList<String>();
        mUpvotersList = new ArrayList<String>();
    }

    /** This constructor is called to convert an object retrieved from the database into a PhotoObject.     */
    public PhotoObject(String fullSizeImageLink, Bitmap thumbnail, String pictureId, String authorID, String photoName, long createdDate,
                       long expireDate, double latitude, double longitude, int nbUpvotes, int nbDownvotes, List<String> upvoters,
                       List<String> downvoters){
        mFullsizeImage = null;
        mHasFullsizeImage=false;
        mFullsizeImageLink=fullSizeImageLink;
        mThumbnail = thumbnail;
        mPictureId = pictureId;
        mPhotoName = photoName;
        mCreatedDate = new Timestamp(createdDate);
        mExpireDate = new Timestamp(expireDate);
        mLatitude = latitude;
        mLongitude = longitude;
        mAuthorID = authorID;
        mStoredInternally = false;
        mNbUpvotes = nbUpvotes;
        mNbDownvotes = nbDownvotes;
        mRadius = computeRadius();
        mUpvotersList = new ArrayList<>(upvoters);
        mDownvotersList = new ArrayList<>(downvoters);
    }




//FUNCTIONS PROVIDED BY THIS CLASS

    /** uploads the object to our online services
     *  prove
     */
    public void upload(boolean hasListener, OnCompleteListener completionListener){
        // sendToFileServer calls sendToDatabase on success
        sendToFileServer(hasListener, completionListener);
    }

    /** return true if the coordinates in parameters are in the scope of the picture}
     */
    public boolean isInPictureCircle(LatLng position){
        return computeDistanceBetween(
                new LatLng(mLatitude, mLongitude),
                position
        ) <= mRadius;
    }

    public String processVote(int vote, String votersId){
        String toastText="";   // message that will be displayed as the action's result
        boolean voteIsValid=false;
        if(this.getAuthorId().equals(votersId)){
            toastText="You can't vote for your own photo!";
        }else if(this.getUpvotersList().contains(votersId) && vote==1) {   // illegal upvote
            toastText = "you already upvoted this image !";
        }else if(this.getDownvotersList().contains(votersId) && vote==-1){ // illegal downvote
            toastText = "you already downvoted this image !";
        }else{
            if(vote == 1) {
                voteIsValid=true;
                toastText = "upvoted !";
            }else if(vote == -1) {
                voteIsValid=true;
                toastText = "downvoted !";
            }else {
                throw new IllegalArgumentException("votes should be either 1 (upvote) or -1 (downvote)");
            }
        }

        if(voteIsValid) {
            if (vote == -1) {
                if (this.mUpvotersList.contains(votersId)) { //need to remove user's previous upvote
                    mNbUpvotes -= 1;
                }
                mNbDownvotes += 1;
                mDownvotersList.add(votersId);
                mUpvotersList.remove(votersId);
            } else if (vote == 1) {
                if (this.mDownvotersList.contains(votersId)) { //need to remove user's previous downvote
                    mNbDownvotes -= 1;
                }
                mNbUpvotes += 1;
                mUpvotersList.add(votersId);
                mDownvotersList.remove(votersId);
            }

            this.computeRadius();

            // push changes to Database
            DatabaseReference DBref = DatabaseRef.getMediaDirectory();
            DBref.child(mPictureId).child("upvotes").setValue(mNbUpvotes);
            DBref.child(mPictureId).child("downvotes").setValue(mNbDownvotes);
            DBref.child(mPictureId).child("upvotersList").setValue(mUpvotersList);
            DBref.child(mPictureId).child("downvotersList").setValue(mDownvotersList);
        }

        return toastText;
    }

    /** retrieves the fullsizeimage from the fileserver and caches it in the object.
     *  Offers the caller to pass some listeners to trigger custom actions on download success or failure.
     *  Booleans
     */
    public void retrieveFullsizeImage(boolean hasOnCompleteListener, OnCompleteListener completionListener) throws IllegalArgumentException{
        // check for necessary conditions
        if(mFullsizeImageLink==null){
            throw new AssertionError("if there is no image stored, object should have a link to retrieve it");
        }

        // Create a file retrieval task
        StorageReference gsReference = null;
        try {
            gsReference = FirebaseStorage.getInstance().getReferenceFromUrl(mFullsizeImageLink);
        } catch (IllegalArgumentException e){
            // getReferenceFromUrl throws an IllegalArgumentExceptino if mFullsizeImageLink isn't a valid firebase link
            throw new IllegalArgumentException("Retrieving from improper Firebase Storage link "+mFullsizeImageLink);
        }
        final long ONE_MEGABYTE = 1024 * 1024;
        Task<byte[]> retrieveFullsizeImageFromFileserver = gsReference.getBytes(ONE_MEGABYTE);

        // add default listener to cache the obtained image
        addRetrieveFullsizeImageDefaultListeners(retrieveFullsizeImageFromFileserver);

        // add optionnal (user's) listener
        if(hasOnCompleteListener){
            if(completionListener==null){
                throw new NullPointerException("this listener is specified not to be null !");
            }
            retrieveFullsizeImageFromFileserver.addOnCompleteListener(completionListener);
        }
    }




//ALL THE GETTER FUNCTIONS

    public boolean hasFullSizeImage(){
        return mHasFullsizeImage;
    }
    public Bitmap getFullSizeImage(){
        if(mHasFullsizeImage){
            return mFullsizeImage.copy(mFullsizeImage.getConfig(), true);
        }else{
            throw new NoSuchElementException("PhotoObject doesn't have a fullsizeImage - you need to retrieve it with the retrieveFullSizeImage - hint : use asFullSizeImage to avoid this problem");
        }
    }
    public String getPhotoName(){
        return mPhotoName;
    }
    public Timestamp getCreatedDate(){
        return new Timestamp(mCreatedDate.getTime());
    }
    public Timestamp getExpireDate(){
        return new Timestamp(mExpireDate.getTime());
    }
    public double getLatitude(){
        return mLatitude;
    }
    public double getLongitude(){
        return mLongitude;
    }
    public int getRadius(){
        return mRadius;
    }
    public String getAuthorId(){
        return mAuthorID;
    }
    public Bitmap getThumbnail(){
        return mThumbnail.copy(mThumbnail.getConfig(), true);
    }
    public String getPictureId() {
        return mPictureId;
    }
    public String getFullsizeImageLink() {
        return mFullsizeImageLink;
    }
    public boolean getStoredInternallyStatus(){
        return mStoredInternally;
    }
    public int getUpvotes(){return mNbUpvotes;}
    public int getDownvotes(){return mNbDownvotes;}
    public List<String> getUpvotersList(){ return Collections.unmodifiableList(mUpvotersList); }
    public List<String> getDownvotersList(){ return Collections.unmodifiableList(mDownvotersList); }


//SETTER FUNCTIONS

    public void setStoredInternallyStatus(boolean storedInternally){
        mStoredInternally = storedInternally;
    }


// PRIVATE HELPERS USED IN THE CLASS ONLY

    /** Computes the radius of the image according to its popularity and automatically updates the value
     */
    private int computeRadius(){
        if(mNbDownvotes+mNbUpvotes==0){
            throw new AssertionError(" upvotes+downvoted=0 => the PhotoObject was not initialized correctly\n"+this.toString());
        }
        double upvotesRatio = (double)mNbUpvotes / (double)(mNbDownvotes+mNbUpvotes);       // in ]0, 1[
        double downvotesRatio = (double)mNbDownvotes / (double)(mNbDownvotes+mNbUpvotes);   // in ]0, 1[
        if(upvotesRatio<=0 || upvotesRatio>=1 || downvotesRatio<=0 || downvotesRatio>=1){
            throw new AssertionError("up/down votes ratio should be in ]0, 1[\n"+this.toString());
        }
        double popularityRatio = upvotesRatio - downvotesRatio;             // in [-1, 1]
        int resultingRadius = DEFAULT_VIEW_RADIUS;
        if(popularityRatio>0){
            resultingRadius =  (int)Math.ceil(DEFAULT_VIEW_RADIUS + popularityRatio*(MAX_VIEW_RADIUS-DEFAULT_VIEW_RADIUS));  // scale between default and max if popular
        }else if (popularityRatio<0){
            double unpopularityRatio = -popularityRatio;
            resultingRadius =  (int)Math.ceil(MIN_VIEW_RADIUS + unpopularityRatio*(DEFAULT_VIEW_RADIUS-MIN_VIEW_RADIUS));  // scale between min and default if unpopular
        }
        if(resultingRadius < MIN_VIEW_RADIUS){
            throw new AssertionError("can't be < MIN_VIEW_RADIUS : computed "+resultingRadius+"\n"+this.toString());
        }
        mRadius=resultingRadius;
        return resultingRadius;
    }

    /**
     * Adds default listeners to a query, which will :
     *  - store the fullsizeImage in the object once it is retrieved
     *  - handle failures
     */
    /**
     * Adds default listeners, which will :
     *  - store the fullsizeImage in the object once it is retrieved
     *  - handle failures
     */
    private void addRetrieveFullsizeImageDefaultListeners(Task fileServerTask){
        fileServerTask.addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/PictureID.jpg" is returns, use this as needed
                mFullsizeImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mHasFullsizeImage = true;
                Log.d("DownloadFromFileServer", "Downloaded full size image from FileServer");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e("DownloadFromFileServer", "Exception raised by getFromFileServer()");
            }
        });
    }


    /** converts This into an object that we can store in the database, by converting the thumbnail into a String
     *  and leaving behing the fullsizeImage (which should be uploaded in fileserver, and retrieveable through the mFullsizeImageLink)
     */
    private PhotoObjectStoredInDatabase convertForStorageInDatabase(){
        if(mFullsizeImageLink==null){
            throw new AssertionError("the link should have been set after sending the fullsizeImage to fileserver - don't call this function on its own");
        }
        String linkToFullsizeImage = mFullsizeImageLink;
        String thumbnailAsString = encodeBitmapAsString(mThumbnail);
        return new PhotoObjectStoredInDatabase(linkToFullsizeImage, thumbnailAsString, mPictureId,mAuthorID, mPhotoName,
                mCreatedDate, mExpireDate, mLatitude, mLongitude, mNbUpvotes, mNbDownvotes, mUpvotersList, mDownvotersList);
    }

    /** encodes the passed bitmap into a string
     */
    private String encodeBitmapAsString(Bitmap img){
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        //TODO img.recycle() ??;
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    /** creates the thumbail from this object by reducing the resolution of the fullSizeImage
     */
    private Bitmap createThumbnail(Bitmap fullSizeImage){
        return ThumbnailUtils.extractThumbnail(fullSizeImage, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
    }

    @Override
    public String toString(){
        return "PhotoObject: "+mPictureId+
                "\n  ---  author : "+mAuthorID+
                "\n  ---  name : "+mPhotoName+
                "\n  ---  pos : ("+mLatitude+","+mLongitude+")"+
                "\n  ---  up/down votes : "+mNbUpvotes+", "+mNbDownvotes+
                "\n  ---  radius : "+mRadius
                ;
    }

    /** Send the full size image to the file server to be stored
     */
    private void sendToFileServer(final boolean hasListener, final OnCompleteListener completionListener) {
        // Create a storage reference from our app
        StorageReference storageRef = StorageRef.getMediaDirectory();

        // Create a reference to "PictureID.jpg"
        StorageReference pictureRef = storageRef.child(mPictureId + ".jpg");

        // Create a reference to 'images/"PictureID".jpg'
        StorageReference pictureImagesRef = storageRef.child("images/" + mPictureId +  ".jpg");

        // Convert the bitmap image to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mFullsizeImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // upload the file
        pictureRef.putBytes(data).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("UploadFileToFileServer", "Exception raised by sendToFileServer()");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                // get the download link of the file
                mFullsizeImageLink = taskSnapshot.getDownloadUrl().toString();
                sendToDatabase(hasListener, completionListener);
            }
        });
    }

    /** Stores the object into the database (with intermediary steps : storing fullSIzeImage in the fileServer, converting the object into a PhotoObjectStoredInDatabase)
    * It is the responsibility of the sender to use the correct DBref, accordingly with the pictureId chosen, since the object will be used in a child named after the pictureId
    */
    private void sendToDatabase(boolean hasListener, OnCompleteListener completionListener){
        DatabaseReference DBref = DatabaseRef.getMediaDirectory();
        PhotoObjectStoredInDatabase DBobject = this.convertForStorageInDatabase();
        if(hasListener) {
            if(completionListener==null) {
                throw new AssertionError("The listener was declared to be non-null");
            }
            DBref.child(mPictureId).setValue(DBobject).addOnCompleteListener(completionListener);
        }else {
            DBref.child(mPictureId).setValue(DBobject);
        }
    }

}

