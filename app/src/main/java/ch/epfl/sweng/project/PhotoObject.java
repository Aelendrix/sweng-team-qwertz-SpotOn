package ch.epfl.sweng.project;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class PhotoObject {

    private final long DEFAULT_PICTURE_LIFETIME = 24*60*60*1000; // in milliseconds - 24H
    private final int THUMBNAIL_SIZE = 128; // in pixels
    private final String PATH_TO_MEDIA_DIRECTORY = "MediaDirectory";

    private DatabaseReference DBreference;

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
    public PhotoObject(Bitmap fullSizePic, String authorID, String photoName, Timestamp createdDate,
                       double latitude, double longitude, int radius, FirebaseDatabase DB){
        this.DBreference = DB.getReference(PATH_TO_MEDIA_DIRECTORY);
        this.fullSizeImage = fullSizePic.copy(fullSizePic.getConfig(), true);
        this.hasFullSizeImage=true;
        this.thumbnail = createThumbnail(fullSizeImage);
        this.pictureId = DBreference.push().getKey();   //available even offline
        this.photoName = photoName;
        this.createdDate = createdDate;
        this.expireDate = new Timestamp(createdDate.getTime()+DEFAULT_PICTURE_LIFETIME);
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.authorID = authorID;
    }

    // TODO : comment - constructor used by PhotoOBjectStoredInDatabase
    public PhotoObject(Bitmap thumbnail, String authorID, String pictureId, String photoName, Timestamp createdDate,
                       Timestamp expireDate, double latitude, double longitude, int radius, FirebaseDatabase DB){
        this.DBreference = DB.getReference(PATH_TO_MEDIA_DIRECTORY);
        this.fullSizeImage = null;
        this.hasFullSizeImage=false;
        this.thumbnail = createThumbnail(fullSizeImage);
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

    public void sendToDatabase(){
        System.out.println("called send fonction");
        PhotoObjectStoredInDatabase DBobject = this.convertForStorageInDatabase();
        System.out.println("converted to database object - pictureID="+DBobject.getPictureId());
        DBreference.child(this.pictureId).setValue(DBobject);
        System.out.println("sent");
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
        // TODO obtain link for fullSizeImage in fileserver
        String linkToFullSizeImage = "PLACEHOLDER";
        String thumbnailAsString = encodeBitmapAsString(this.thumbnail);
        return new PhotoObjectStoredInDatabase(this.DBreference, thumbnailAsString, this.pictureId, this.authorID, this.photoName,
                this.createdDate, this.expireDate, this.latitude, this.longitude, this.radius);
    }

    private String encodeBitmapAsString(Bitmap img){
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        //TODO img.recycle();
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap createThumbnail(Bitmap fullSizeImage){
        return ThumbnailUtils.extractThumbnail(fullSizeImage, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
    }

}