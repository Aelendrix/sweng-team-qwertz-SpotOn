package ch.epfl.sweng.project;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

/**
 *  @author Quentin
 *  This class represents a picture and all associated informations : name, thumbnail, pictureID, author, timestamps, position, radius
 *  It is mean to be used locally, as opposed to the PhotoObjectStoredInDatabase which is used to be stored in the database
 */
public class PhotoObject {

    private final long DEFAULT_PICTURE_LIFETIME = 24*60*60*1000; // in milliseconds - 24H
    private final int THUMBNAIL_SIZE = 128; // in pixels

    private Bitmap fullSizeImage;
    private String fullSizeImageLink;
    private boolean hasFullSizeImage;   // false -> no Image, but has link, true -> has image, can't access link (don't need it => no getter)
    private Bitmap thumbnail;
    private String pictureId;
    private String authorID;
    private String photoName;
    private Timestamp createdDate;
    private Timestamp expireDate;
    private double latitude;
    private double longitude;
    private int radius;

    /** This constructor will be used when the used takes a photo with his device, and create the object from locally obtained informations
     *  pictureId should be created by calling .push().getKey() on the DatabaseReference where the object should be stored.
     */
    public PhotoObject(Bitmap fullSizePic, String pictureId, String authorID, String photoName,
                       Timestamp createdDate, double latitude, double longitude, int radius){
        this.fullSizeImage = fullSizePic.copy(fullSizePic.getConfig(), true);
        this.hasFullSizeImage=true;
        this.fullSizeImageLink = null;  // there is no need for a link
        this.thumbnail = createThumbnail(fullSizeImage);
        this.pictureId = pictureId;   //available even offline
        this.photoName = photoName;
        this.createdDate = createdDate;
        this.expireDate = new Timestamp(createdDate.getTime()+DEFAULT_PICTURE_LIFETIME);
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.authorID = authorID;
    }

    /** This constructor is called to convert an object retrieved from the database into a PhotoObject.
     */
    public PhotoObject(String fullSizeImageLink, Bitmap thumbnail, String pictureId, String authorID, String photoName, Timestamp createdDate,
                       Timestamp expireDate, double latitude, double longitude, int radius){
        this.fullSizeImage = null;
        this.hasFullSizeImage=false;
        this.fullSizeImageLink=fullSizeImageLink;
        this.thumbnail = thumbnail;
        this.pictureId = pictureId;
        this.photoName = photoName;
        this.createdDate = createdDate;
        this.expireDate = expireDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.authorID = authorID;
    }


//FUNCTIONS PROVIDED BY THIS CLASS

    // Stores the object into the databse (with intermediary steps : storing fullSIzeImage in the fileServer, converting the object into a PhotoObjectStoredInDatabase)
    // It is the responsability of the sender to use the correct DBref, accordingly with the pictureId chosen, since the object will be used in a child named after the pictureId
    public void sendToDatabase(DatabaseReference DBref){
        PhotoObjectStoredInDatabase DBobject = this.convertForStorageInDatabase();
        System.out.println(DBobject);
        DBref.child(this.pictureId).setValue(DBobject);
    }

    //return true if the coordinates in parameters are in the scope of the picture
    public boolean isInPictureCircle(double paramLat, double paramLng){
        return computeDistanceBetween(
                new LatLng(latitude, longitude),
                new LatLng( paramLat,paramLng )
        ) <= radius;
    }


//ALL THE GETTER FUNCTIONS

    // This getter functions as a cache, it gets the fullSizeImage only when needed, and stores it for later
    public Bitmap getFullSizeImage() {
        if (hasFullSizeImage) {
            return fullSizeImage.copy(fullSizeImage.getConfig(), true);
        }else{
            if(this.fullSizeImageLink==null){
                throw new AssertionError("if there is no image stored, is should have a link to retrieve it");
            }
            // TODO : get fullSizeImage from file server                                                                <--------
            //hasFullSizeImage = true;
            //return this.getFullSizeImage();
            throw new NoSuchElementException("The code to retrieve the full size image doesn't exist yet");
        }
    }
    public String getPhotoName(){
        return photoName;
    }
    public Timestamp getCreatedDate(){
        return new Timestamp(createdDate.getTime());
    }
    public Timestamp getExpireDate(){
        return new Timestamp(expireDate.getTime());
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public int getRadius(){
        return radius;
    }
    public String getAuthorId(){
        return authorID;
    }
    public Bitmap getThumbnail(){
        return this.thumbnail.copy(this.thumbnail.getConfig(), true);
    }
    public String getPictureId() {
        return pictureId;
    }



// PRIVATE HELPERS USED IN THE CLASS ONLY

    private PhotoObjectStoredInDatabase convertForStorageInDatabase(){
        // TODO obtain link for fullSizeImage in fileserver                                                                 <------
        String linkToFullSizeImage = "/default.jpg";
        String thumbnailAsString = encodeBitmapAsString(this.thumbnail);
        return new PhotoObjectStoredInDatabase(linkToFullSizeImage, thumbnailAsString, this.pictureId, this.authorID, this.photoName,
                this.createdDate, this.expireDate, this.latitude, this.longitude, this.radius);
    }

    private String encodeBitmapAsString(Bitmap img){
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        //TODO img.recycle() ??;
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap createThumbnail(Bitmap fullSizeImage){
        return ThumbnailUtils.extractThumbnail(fullSizeImage, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
    }

}