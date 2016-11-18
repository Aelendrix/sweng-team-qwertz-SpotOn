package ch.epfl.sweng.spotOn.media;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;


import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import ch.epfl.sweng.spotOn.utils.BitmapUtils;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

/**
 *  This class represents a picture and all associated information : name, thumbnail, pictureID, author, timestamps, position, radius
 *  It is mean to be used locally, as opposed to the PhotoObjectStoredInDatabase which is used to be stored in the database
 */
public class PhotoObject {

    private final int THUMBNAIL_SIZE = 128; // in pixels

    public final static int MAX_VIEW_RADIUS = 7000;    // in meters
    public final static int DEFAULT_VIEW_RADIUS = 70;
    public final static int MIN_VIEW_RADIUS = 20;

    public final static long MAX_LIFETIME = 3*24*60*60*1000 ;              // in ms - 72h
    public final static long DEFAULT_LIFETIME = 24*60*60*1000;     // in milliseconds - 24H
    public final static long MIN_LIFETIME = 2*60*60*1000;                  // in ms - 2h

    private final int DOWNVOTE_KARMA_GIVEN = -5;
    private final int UPVOTE_KARMA_GIVEN = 10;

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
    private boolean mStoredInServer;
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
        mThumbnail = BitmapUtils.createThumbnail(mFullsizeImage, THUMBNAIL_SIZE);
        mPictureId = DatabaseRef.getMediaDirectory().push().getKey();   //available even offline
        mPhotoName = photoName;
        mCreatedDate = createdDate;
        mLatitude = latitude;
        mLongitude = longitude;
        mAuthorID = authorID;
        mStoredInternally = false;
        mStoredInServer = false;
        mNbUpvotes = 1;     // initialize at 1 to avoid any possible division by 0 later
        mNbDownvotes = 1;
        mDownvotersList = new ArrayList<String>();
        mUpvotersList = new ArrayList<String>();
        this.computeRadius();
        this.computeExpireDate();
    }

    /** This constructor is called to convert an object retrieved from the database into a PhotoObject.     */
    public PhotoObject(String fullSizeImageLink, Bitmap thumbnail, String pictureId, String authorID, String photoName, long createdDate,
                       double latitude, double longitude, int nbUpvotes, int nbDownvotes, List<String> upvoters,
                       List<String> downvoters){
        mFullsizeImage = null;
        mHasFullsizeImage=false;
        mFullsizeImageLink=fullSizeImageLink;
        mThumbnail = thumbnail;
        mPictureId = pictureId;
        mPhotoName = photoName;
        mCreatedDate = new Timestamp(createdDate);
        mLatitude = latitude;
        mLongitude = longitude;
        mAuthorID = authorID;
        mStoredInternally = false;
        mStoredInServer = false;
        mNbUpvotes = nbUpvotes;
        mNbDownvotes = nbDownvotes;
        mUpvotersList = new ArrayList<>(upvoters);
        mDownvotersList = new ArrayList<>(downvoters);
        this.computeRadius();
        this.computeExpireDate();
    }




//FUNCTIONS PROVIDED BY THIS CLASS

    /** uploads the object to our online services
     *  prove
     */
    public void upload(boolean hasListener, OnCompleteListener completionListener){
        // sendToFileServer calls sendToDatabase on success
        sendToFileServer(hasListener, completionListener);
    }
    public void upload(){
        upload(false, null);
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
        int karmaAdded = 0;    // karma given to the photo's author
        if(mAuthorID.equals(votersId)){
            toastText="You can't vote for your own photo!";
        }else if(mUpvotersList.contains(votersId) && vote==1) {   // illegal upvote
            toastText = "you already upvoted this image !";
        }else if(mDownvotersList.contains(votersId) && vote==-1){ // illegal downvote
            toastText = "you already downvoted this image !";
        }else{
            if(vote == 1) {
                voteIsValid=true;
                toastText = "upvoted !";
                karmaAdded = UPVOTE_KARMA_GIVEN;
            }else if(vote == -1) {
                voteIsValid=true;
                toastText = "downvoted !";
                karmaAdded = DOWNVOTE_KARMA_GIVEN;
            }else {
                throw new IllegalArgumentException("votes should be either 1 (upvote) or -1 (downvote)");
            }
        }

        if(voteIsValid) {
            if (vote == -1) {
                if (mUpvotersList.contains(votersId)) { //need to remove user's previous upvote and get back the karma from that upvote
                    mNbUpvotes -= 1;
                    karmaAdded -= UPVOTE_KARMA_GIVEN;
                }
                mNbDownvotes += 1;
                mDownvotersList.add(votersId);
                mUpvotersList.remove(votersId);
            } else if (vote == 1) {
                if (mDownvotersList.contains(votersId)) { //need to remove user's previous downvote and give back karma from that downvote
                    mNbDownvotes -= 1;
                    karmaAdded -= DOWNVOTE_KARMA_GIVEN;
                }
                mNbUpvotes += 1;
                mUpvotersList.add(votersId);
                mDownvotersList.remove(votersId);
            }

            computeRadius();
            computeExpireDate();

            // push changes to Database if the object was uploaded
            if(mFullsizeImageLink!=null) {
                DatabaseReference DBref = DatabaseRef.getMediaDirectory();
                DBref.child(mPictureId).child("upvotes").setValue(mNbUpvotes);
                DBref.child(mPictureId).child("downvotes").setValue(mNbDownvotes);
                DBref.child(mPictureId).child("upvotersList").setValue(mUpvotersList);
                DBref.child(mPictureId).child("downvotersList").setValue(mDownvotersList);
                DBref.child(mPictureId).child("expireDate").setValue(mExpireDate.getTime());
                giveAuthorHisKarma(karmaAdded);
            }
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
        final long TWO_MEGABYTE = 2 * 1024 * 1024;
        Task<byte[]> retrieveFullsizeImageFromFileserver = gsReference.getBytes(TWO_MEGABYTE);

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
    public boolean isStoredInternally(){
        return mStoredInternally;
    }
    public boolean isStoredInServer() {
        return mStoredInServer;
    }
    public int getUpvotes(){return mNbUpvotes;}
    public int getDownvotes(){return mNbDownvotes;}
    public List<String> getUpvotersList(){ return Collections.unmodifiableList(mUpvotersList); }
    public List<String> getDownvotersList(){ return Collections.unmodifiableList(mDownvotersList); }


//SETTER FUNCTIONS

    public void setStoredInternallyStatus(boolean storedInternally){
        mStoredInternally = storedInternally;
    }

    public void setSentToServerStatus(boolean alreadySent){
        mStoredInServer = alreadySent;
    }


// PRIVATE HELPERS USED IN THE CLASS ONLY

    /** Computes a popularity ratio, that belongs in ]-1,1[, -1 being the lest popular and 1 the most popular
    */
    private double computePopularityRatio(){
        if(mNbDownvotes+mNbUpvotes==0){
            throw new AssertionError(" upvotes+downvoted=0 => the PhotoObject was not initialized correctly\n"+this.toString());
        }
        /* doing it this way gave rounding errors for (1,4)... weird ?
        double upvotesRatio = (double)mNbUpvotes / (double)(mNbDownvotes+mNbUpvotes);       // in ]0, 1[
        double downvotesRatio = (double)mNbDownvotes / (double)(mNbDownvotes+mNbUpvotes);   // in ]0, 1[
        if(upvotesRatio<=0 || upvotesRatio>=1 || downvotesRatio<=0 || downvotesRatio>=1){
            throw new AssertionError("up/down votes ratio should be in ]0, 1[\n"+this.toString());
        }
        double popularityRatio = upvotesRatio - downvotesRatio;             // in [-1, 1]
        */
        double popularityRatio = (double)(mNbUpvotes-mNbDownvotes) / (double)(mNbDownvotes+mNbUpvotes);
        return popularityRatio;
    }

    /** Computes the radius of the image according to its popularity and automatically updates the value
     */
    private int computeRadius(){
        int computedRadius = DEFAULT_VIEW_RADIUS;
        double popularityRatio = computePopularityRatio();
        if(popularityRatio>0){
            computedRadius =  (int)Math.ceil(DEFAULT_VIEW_RADIUS + popularityRatio*(MAX_VIEW_RADIUS-DEFAULT_VIEW_RADIUS));  // scale between default and max if popular
        }else if (popularityRatio<0){
            double unpopularityRatio = -popularityRatio;
            computedRadius =  (int)Math.ceil(MIN_VIEW_RADIUS + unpopularityRatio*(DEFAULT_VIEW_RADIUS-MIN_VIEW_RADIUS));  // scale between min and default if unpopular
        }
        if(computedRadius < MIN_VIEW_RADIUS){
            throw new AssertionError("can't be < MIN_LIFETIME : computed "+computedRadius+"\n"+this.toString());
        }
        mRadius=computedRadius;
        return computedRadius;
    }

    private Timestamp computeExpireDate(){
        double popularityRatio = computePopularityRatio();
        long computedLifetime = DEFAULT_LIFETIME;
        if(popularityRatio>0){
            computedLifetime = (int)Math.ceil(DEFAULT_LIFETIME + popularityRatio*(MAX_LIFETIME-DEFAULT_LIFETIME));      // scale between default and max if popular
        }else if(popularityRatio<0){
            double unpopularityRatio = -popularityRatio;
            computedLifetime = (int)Math.ceil(MIN_LIFETIME + unpopularityRatio*(DEFAULT_LIFETIME-MIN_LIFETIME));        // scale between min and default if unpopular
        }
        if(computedLifetime<MIN_LIFETIME){
            throw new AssertionError("can't be < MIN_LIFETIME : computed "+computedLifetime+"\n"+this.toString());
        }
        mExpireDate = new Timestamp(mCreatedDate.getTime()+computedLifetime);
        return mExpireDate;
    }

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
        String thumbnailAsString = BitmapUtils.encodeBitmapAsString(mThumbnail);
        return new PhotoObjectStoredInDatabase(linkToFullsizeImage, thumbnailAsString, mPictureId,mAuthorID, mPhotoName,
                mCreatedDate, mExpireDate, mLatitude, mLongitude, mNbUpvotes, mNbDownvotes, mUpvotersList, mDownvotersList);
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
                mStoredInServer = true;
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

    private void giveAuthorHisKarma(final int addedKarma){
        final DatabaseReference DBref = DatabaseRef.getUsersDirectory();
        DBref.orderByChild("userId").equalTo(mAuthorID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(dataSnapshot.child(mAuthorID).child("karma").getValue() == null){
                        DBref.child(mAuthorID).child("karma").setValue(0);
                    }
                    long newKarma = 0;

                    if(dataSnapshot.child(mAuthorID).child("karma").getValue() != null) {
                        newKarma = ((long) dataSnapshot.child(mAuthorID).child("karma").getValue());
                    }
                    newKarma += addedKarma;
                    DBref.child(mAuthorID).child("karma").setValue(newKarma);
                    
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

}

