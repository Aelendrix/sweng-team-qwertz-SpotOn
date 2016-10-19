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

    private String mFullSizePhotoLink;
    private String mThumbnailAsString;
    private String mPictureId;
    private String mAuthorID;
    private String mPhotoName;
    private Timestamp mCreatedDate;
    private Timestamp mExpireDate;
    private double mLatitude;
    private double mLongitude;
    private int mRadius;

    public PhotoObjectStoredInDatabase(){
        // default constructor required to upload object to firebase
    }

    /** Constructor meant to be called by the conversion function in the PhotoObject class     */
    public PhotoObjectStoredInDatabase(String fullSizePhotoLink, String thumbnailAsString, String pictureId, String authorID, String photoName,
                                       Timestamp createdDate, Timestamp expireDate, double latitude, double longitude, int radius){
        mFullSizePhotoLink=fullSizePhotoLink;
        mThumbnailAsString=thumbnailAsString;
        mPictureId=pictureId;
        mAuthorID=authorID;
        mPhotoName=photoName;
        mCreatedDate=createdDate;
        mExpireDate=expireDate;
        mLatitude=latitude;
        mLongitude=longitude;
        mRadius=radius;
    }


// PUBLIC METHODS OFFERED BY THIS CLASS

    // converts the object into a PhotoObject, by converting the thumbnail into a Bitmap
    public PhotoObject convertToPhotoObject(){
        //TODO CONVERT THUMBNAIL
        Bitmap thumbnail = convertStringToBitmapImage(mThumbnailAsString);
        return new PhotoObject(mFullSizePhotoLink, thumbnail, mPictureId, mAuthorID, mPhotoName, mCreatedDate,
                mExpireDate, mLatitude, mLongitude, mRadius);
    }

    // rather meant to be used for debug
    public String toString(){
        String result="PhotoOBject";
        result+="   ---   pictureID="+mPictureId;
        result+="   ---   fullSizePhotoLink="+mFullSizePhotoLink;
        result+="   ---   authorID="+mAuthorID;
        result+="   ---   photoName="+mPhotoName;
        result+="   ---   createdDate="+mCreatedDate+"   ---   expireDate="+mExpireDate+"   ---   pos=("+mLatitude+", "+mLongitude+")   ---   radius="+mRadius;
        result+="   ---   thumbnailAsString lentgh="+mThumbnailAsString.length();
        return result;
    }


// PRIVATE METHODS FOR USE IN THE CLASS ONLY

    // converte a bitmap to a String
    private Bitmap convertStringToBitmapImage(String s){
        byte[] stringByteArray = Base64.decode(s, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(stringByteArray, 0, stringByteArray.length);
    }


// GETTERS REQUIRED (PUBLIC) BY FIREBASE

    public String getFullSizePhotoLink(){ return mFullSizePhotoLink;}
    public String getThumbnailAsString(){ return mThumbnailAsString;}
    public String getPictureId(){ return mPictureId;}
    public String getAuthorID(){ return mAuthorID;}
    public String getPhotoName(){ return mPhotoName;}
    public Timestamp getCreatedDate() { return mCreatedDate; }
    public Timestamp getExpireDate() { return mExpireDate; }
    public double getLatitude(){return mLatitude;}
    public double getLongitude(){return mLongitude;}
    public int getRadius(){ return mRadius;}

}
