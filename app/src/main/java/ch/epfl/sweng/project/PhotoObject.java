package ch.epfl.sweng.project;


import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class PhotoObject {

    // in ms
    private final long DEFAULT_PICTURE_LIFETIME = 24*60*60*1000; //24H

    private String name;
    private Timestamp createdDate;
    private Timestamp expireDate;
    private double latitude;
    private double longitude;
    private int pictureId;
    private int radius;
    private int user;
    private String fullImgLink;
    private String thumbImgLink;

    Bitmap thumbnailImg;
    boolean hasThumbImage;
    Bitmap fullSizeImg;
    boolean hasFullSizeImage;

    public PhotoObject(){
        //constructor needed for firebase object upload
    }

    public PhotoObject(String name,Timestamp createdDate,
                       double latitude, double longitude, int pictureId, int radius,
                       int user, String fullImgLink, String thumbImgLink){
        this.name = name;
        this.createdDate = createdDate;
        expireDate = new Timestamp(createdDate.getTime()+DEFAULT_PICTURE_LIFETIME);
        this.latitude = latitude;
        this.longitude = longitude;
        this.pictureId = pictureId;
        this.radius = radius;
        this.user = user;
        this.fullImgLink=fullImgLink;
        this.thumbImgLink=thumbImgLink;
    }

    public boolean sendToDatabase() {
        //send fullSizeImage to fileServer
        fullSizeImg = null;
        hasFullSizeImage = false;
        //return false if an error occured
        return true;
    }

    //ALL THE GETTER FUNCTIONS

    //TODO: do we constrain here if you location is out of the range of the picture?
    public Bitmap getFullSizeImage() {
        if (hasFullSizeImage) {
            return fullSizeImg;
        }else{
        //fetch full size image from file server
        hasFullSizeImage = true;
        return fullSizeImg;
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
    public int getPictureId(){
        return pictureId;
    }
    public int getRadius(){
     return radius;
    }
    public int getAuthorId(){
        return user;
    }
    public String getFullImgLink(){
        return fullImgLink;
    }
    public String getThumbImgLink(){
        return thumbImgLink;
    }

    //HELPER FUNCTION

    //return true if the coordinates in parameters are in the scope of the picture
    public boolean isInPictureCircle(double paramLat, double paramLng){
        return computeDistanceBetween(
                new LatLng(latitude, longitude),
                new LatLng( paramLat,paramLng )
        ) <= radius;
    }
}
