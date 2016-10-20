package ch.epfl.sweng.project;


import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;
import java.util.NoSuchElementException;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class PhotoObject {

    // in ms
    private final long DEFAULT_PICTURE_LIFETIME = 24*60*60*1000; //24H
    private final int THUMBNAIL_SIZE = 128; // in pixels

    private String name;
    private Timestamp createdDate;
    private Timestamp expireDate;
    private double latitude;
    private double longitude;
    private int radius;
    private int userID;
    private int likes;
    Bitmap thumbnail;    // thumbnail will be stored in database, so it will always exist -> no need for associated boolean variable

    Bitmap fullSizeImage;
    boolean hasFullSizeImage;

    // the following 3 are set according to the database answers when uploading
    private int pictureId;
    private boolean hasPictureId;
    private String fullSizeImageLink;
    private boolean hasFullSizeImageLink;


    public PhotoObject(){
        // default constructor needed for firebase object upload
    }

    public PhotoObject(Bitmap fullSizePic, String name, Timestamp createdDate, double longitude, double latitude, int userID){
        this.fullSizeImage = fullSizePic.copy(fullSizePic.getConfig(), true);
        this.hasFullSizeImage=true;

        this.name = name;
        this.createdDate = createdDate;
        this.expireDate = new Timestamp(createdDate.getTime()+DEFAULT_PICTURE_LIFETIME);
        this.latitude = latitude;
        this.longitude = longitude;
        radius = 500;
        likes = 0;
        this.userID = userID;
        this.thumbnail = createThumbnail(fullSizeImage);

        this.hasFullSizeImageLink=false;
        this.hasPictureId=false;
    }

    public boolean sendToDatabase() {
        // TODO : send fullSizeImage to fileServer
        // TODO : set pictureID, fullSizeImageLink, thumbnailLink
        fullSizeImage = null;
        hasFullSizeImage = false;
        //return false if an error occured
        return true;
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
        return name;
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
    public int getAuthorId(){
        return userID;
    }
    public Bitmap getThumbnail(){
        return thumbnail.copy(thumbnail.getConfig(), true);
    }
    public boolean hasFullSizeImageLink(){
        return hasFullSizeImageLink;
    }
    public String getFullSizeImageLink(){
        if(hasFullSizeImageLink){
            return fullSizeImageLink;
        }else{
            throw new NoSuchElementException("This object doesn't have a fullSizeImageLink (it has probably not been uploaded in the database yet");
        }
    }
    public boolean hasPictureId(){
        return hasPictureId;
    }
    public int getPictureId() {
        if (hasPictureId){
            return pictureId;
        }else{
            throw new NoSuchElementException("This object doesn't have a pictureID (it has probably not been uploaded in the database yet");
        }
    }

    //HELPER FUNCTION

    private Bitmap createThumbnail(Bitmap fullSizeImage){
        return ThumbnailUtils.extractThumbnail(fullSizeImage, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
    }

    //return true if the coordinates in parameters are in the scope of the picture
    public boolean isInPictureCircle(double paramLat, double paramLng){
        return computeDistanceBetween(
                new LatLng(latitude, longitude),
                new LatLng( paramLat,paramLng )
        ) <= radius;
    }

    //increment radius size depending on likes of the photo(+ 5 meters/like)
    public void augmentRadiusSize(){
        radius += likes * 5;
    }
}
