package ch.epfl.sweng.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.sql.Timestamp;

/**
 *  This class represents a photoObject in a form that allows it to be sent to a database.
 *  It is necessary since we can't send raw bitmaps (we convert thumbnail to a string) into the database
 *  It provides a method to convert it to a PhotoOBject, which is the only method that should be used
 */
public class PhotoObjectStoredInDatabase {

    private String fullSizePhotoLink;
    private String thumbnailAsString;
    private String pictureId;
    private String authorID;
    private String photoName;
    private Timestamp createdDate;
    private Timestamp expireDate;
    private double latitude;
    private double longitude;
    private int radius;

    public PhotoObjectStoredInDatabase(){
        // default constructor required to upload object to firebase
    }

    /** Constructor meant to be called by the conversion function in the PhotoObject class     */
    public PhotoObjectStoredInDatabase(String fullSizePhotoLink, String thumbnailAsString, String pictureId, String authorID, String photoName,
                                       Timestamp createdDate, Timestamp expireDate, double latitude, double longitude, int radius){
        this.fullSizePhotoLink=fullSizePhotoLink;
        this.thumbnailAsString=thumbnailAsString;
        this.pictureId=pictureId;
        this.authorID=authorID;
        this.photoName=photoName;
        this.createdDate=createdDate;
        this.expireDate=expireDate;
        this.latitude=latitude;
        this.longitude=longitude;
        this.radius=radius;
    }


// PUBLIC METHODS OFFERED BY THIS CLASS

    // converts the object into a PhotoObject, by converting the thumbnail into a Bitmap
    public PhotoObject convertToPhotoObject(){
        //TODO CONVERT THUMBNAIL
        Bitmap thumbnail = convertStringToBitmapImage(this.thumbnailAsString);
        return new PhotoObject(this.fullSizePhotoLink, thumbnail, this.pictureId, this.authorID, this.photoName, this.createdDate,
                this.expireDate, this.latitude, this.longitude, this.radius);
    }

    // rather meant to be used for debug
    public String toString(){
        String result="PhotoOBject";
        result+="   ---   pictureID="+pictureId;
        result+="   ---   fullSizePhotoLink="+fullSizePhotoLink;
        result+="   ---   authorID="+authorID;
        result+="   ---   photoName="+photoName;
        result+="   ---   createdDate="+createdDate+"   ---   expireDate="+expireDate+"   ---   pos=("+latitude+", "+longitude+")   ---   radius="+radius;
        result+="   ---   thumbnailAsString lentgh="+thumbnailAsString.length();
        return result;
    }


// PRIVATE METHODS FOR USE IN THE CLASS ONLY

    // converte a bitmap to a String
    private Bitmap convertStringToBitmapImage(String s){
        byte[] stringByteArray = Base64.decode(s, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(stringByteArray, 0, stringByteArray.length);
    }


// GETTERS REQUIRED (PUBLIC) BY FIREBASE

    public String getFullSizePhotoLink(){ return this.fullSizePhotoLink;}
    public String getThumbnailAsString(){ return this.thumbnailAsString;}
    public String getPictureId(){ return this.pictureId;}
    public String getAuthorID(){ return this.authorID;}
    public String getPhotoName(){ return this.photoName;}
    public Timestamp getCreatedDate() {
        return this.createdDate;
    }
    public Timestamp getExpireDate() {
        return expireDate;
    }
    public double getLatitude(){
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public int getRadius() {
        return this.radius;
    }

}
