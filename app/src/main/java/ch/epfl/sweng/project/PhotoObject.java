package ch.epfl.sweng.project;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class PhotoObject {

    private final long DEFAULT_PICTURE_LIFETIME = 24*60*60*1000; // in milliseconds - 24H
    private final int THUMBNAIL_SIZE = 128; // in pixels

    private Bitmap fullSizeImage;
    private boolean hasFullSizeImage;
    private Bitmap thumbnail;
    private String pictureId;
    private String authorID;
    private String photoName;
    private Timestamp createdDate;
    private Timestamp expireDate;
    private double latitude;
    private double longitude;
    private int radius;

    // TODO : comment : constructor for when we take a photo
    public PhotoObject(Bitmap fullSizePic, String pictureId, String authorID, String photoName,
                       Timestamp createdDate, double latitude, double longitude, int radius){
        this.fullSizeImage = fullSizePic.copy(fullSizePic.getConfig(), true);
        this.hasFullSizeImage=true;
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

    // TODO : comment - constructor used by PhotoOBjectStoredInDatabase
    public PhotoObject(Bitmap thumbnail, String pictureId, String authorID, String photoName, Timestamp createdDate,
                       Timestamp expireDate, double latitude, double longitude, int radius){
        this.fullSizeImage = null;
        this.hasFullSizeImage=false;
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


//ALL THE GETTER FUNCTIONS

    //TODO: do we constrain here if you location is out of the range of the picture?
    public Bitmap getFullSizeImage() {
        if (hasFullSizeImage) {
            return fullSizeImage.copy(fullSizeImage.getConfig(), true);
        }else{
            // TODO : get fullSizeImage from file server
            hasFullSizeImage = true;
            return this.getFullSizeImage();
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


//FUNCTIONS PROVIDED BY THIS CLASS

    //TODO - comment : should use the same DBref as the one used to obtain the pictureId
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



// PRIVATE HELPERS USED IN THE CLASS ONLY

    private PhotoObjectStoredInDatabase convertForStorageInDatabase(){
        // TODO obtain link for fullSizeImage in fileserver                                                                 <------
        String linkToFullSizeImage = "PLACEHOLDER";
        String thumbnailAsString = encodeBitmapAsString(this.thumbnail);
        return new PhotoObjectStoredInDatabase(thumbnailAsString, this.pictureId, this.authorID, this.photoName,
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