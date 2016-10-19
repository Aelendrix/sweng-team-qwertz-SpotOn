package ch.epfl.sweng.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;

import ch.epfl.sweng.project.PhotoObject;

/**
 * Created by quentin on 18.10.16.
 */

public class PhotoObjectStoredInDatabase {

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

    public PhotoObjectStoredInDatabase(String thumbnailAsString, String pictureId, String authorID, String photoName,
                                       Timestamp createdDate, Timestamp expireDate, double latitude, double longitude, int radius){
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

/*    public PhotoObject convertToPhotoObject(){
        //TODO CONVERT THUMBNAIL
        Bitmap thumbnail = convertStringToBitmapImage(this.thumbnailAsString);
        return new PhotoObject(thumbnail, this.authorID, this.pictureId, this.photoName, this.createdDate,
                this.expireDate, this.latitude, this.longitude, this.radius, this.DBreference.getDatabase());
    }*/

    public String toString(){
        String result="";
        result+="   ---   pictureID="+pictureId;
        result+="   ---   authorID="+authorID;
        result+="   ---   photoName="+photoName;
        result+="   ---   createdDate="+createdDate+"   ---   expireDate="+expireDate+"   ---   pos=("+latitude+", "+longitude+")   ---   radius="+radius;
        result+="   ---   thumbnailStringSize ="+thumbnailAsString.length();
        return result;
    }



// PRIVATE METHODS FOR USE IN THE CLASS ONLY

/*    private Bitmap convertStringToBitmapImage(String s){
        byte[] stringByteArray = Base64.decode(s, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(stringByteArray, 0, stringByteArray.length);
    }*/


// GETTERS REQUIRED (PUBLIC) BY FIREBASE

    public String getThumbnailAsString() { return this.thumbnailAsString;}
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
