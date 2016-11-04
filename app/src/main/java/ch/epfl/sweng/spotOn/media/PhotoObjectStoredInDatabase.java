package ch.epfl.sweng.spotOn.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    private long mCreatedDate;
    private long mExpireDate;
    private double mLatitude;
    private double mLongitude;
    private int mNbUpvotes;
    private int mNbDownvotes;
    private List<String> mUpvotersList;
    private List<String> mDownvotersList;

    public PhotoObjectStoredInDatabase(){
        // default constructor required to upload object to firebase
    }

    /** Constructor meant to be called by the conversion function in the PhotoObject class     */
    public PhotoObjectStoredInDatabase(String fullSizePhotoLink, String thumbnailAsString, String pictureId, String authorID, String photoName,
                                       Timestamp createdDate, Timestamp expireDate, double latitude, double longitude, int upvotes, int downvotes,
                                       List<String> upvotersList, List<String> downvotersList){
        mFullSizePhotoLink=fullSizePhotoLink;
        mThumbnailAsString=thumbnailAsString;
        mPictureId=pictureId;
        mAuthorID=authorID;
        mPhotoName=photoName;
        mCreatedDate=createdDate.getTime();
        mExpireDate=expireDate.getTime();
        mLatitude=latitude;
        mLongitude=longitude;
        mNbUpvotes = upvotes;
        mNbDownvotes = downvotes;
        mUpvotersList = new ArrayList<>(upvotersList);
        mDownvotersList = new ArrayList<>(downvotersList);
    }


// PUBLIC METHODS OFFERED BY THIS CLASS

    // converts the object into a PhotoObject, by converting the thumbnail into a Bitmap
    public PhotoObject convertToPhotoObject(){
        Bitmap thumbnail = convertStringToBitmapImage(mThumbnailAsString);
        List<String> upvotersList;
        List<String> downvotersList;
        if(mUpvotersList == null){
            upvotersList = Collections.emptyList();
        }else{
            upvotersList = new ArrayList<>(mUpvotersList);
        }
        if(mDownvotersList == null){
            downvotersList = Collections.emptyList();
        }else{
            downvotersList = new ArrayList<>((mDownvotersList));
        }
        return new PhotoObject(mFullSizePhotoLink, thumbnail, mPictureId, mAuthorID, mPhotoName, mCreatedDate,
                mExpireDate, mLatitude, mLongitude, mNbUpvotes, mNbDownvotes, upvotersList, downvotersList);
    }

    // rather meant to be used for debug
    protected String getStringDescription(){
        String result="PhotoOBject";
        result+="   ---   pictureID="+mPictureId;
        result+="   ---   fullSizePhotoLink="+mFullSizePhotoLink;
        result+="   ---   authorID="+mAuthorID;
        result+="   ---   photoName="+mPhotoName;
        result+="   ---   createdDate="+mCreatedDate+"   ---   expireDate="+mExpireDate+"   ---   pos=("+mLatitude+", "+mLongitude+")";
        result+="   ---   upvotes="+mNbUpvotes+" downvotes="+mNbDownvotes;
        result+="   ---   voters are:"+mDownvotersList.toString()+mUpvotersList.toString();
        result+="   ---   thumbnailAsString length="+mThumbnailAsString.length();
        return result;
    }


// PRIVATE METHODS FOR USE IN THE CLASS ONLY

    // convert a bitmap to a String
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
    public long getCreatedDate() { return mCreatedDate; }
    public long getExpireDate() { return mExpireDate; }
    public double getLatitude(){return mLatitude;}
    public double getLongitude(){return mLongitude;}
    public int getUpvotes(){return mNbUpvotes;}
    public int getDownvotes(){return mNbDownvotes;}
    public List<String> getUpvotersList(){return mUpvotersList;}
    public List<String> getDownvotersList(){return mDownvotersList;}

    // SETTER REQUIRED (PUBLIC) BY FIREBASE

    public void setFullSizePhotoLink(String fullSizePhotoLink){ mFullSizePhotoLink=fullSizePhotoLink;}
    public void setThumbnailAsString(String thumbnailAsString){mThumbnailAsString=thumbnailAsString;}
    public void setPictureId(String pictureId){mPictureId=pictureId;}
    public void setAuthorID(String authorID){mAuthorID=authorID;}
    public void setPhotoName(String photoName){mPhotoName=photoName;}
    public void setCreatedDate(long createdDate) { mCreatedDate=createdDate; }
    public void setExpireDate(long expireDate) {mExpireDate=expireDate;}
    public void setLatitude(double latitude){mLatitude=latitude;}
    public void setLongitude(double longitude){mLongitude=longitude;}
    public void setUpvotes(int upvotes){mNbUpvotes=upvotes;}
    public void setDownvotes(int downvotes){mNbDownvotes=downvotes;}
    public void setUpvotersList(List<String> upvotersList){mUpvotersList=upvotersList;}
    public void setDownvotersList(List<String> downvotersList){mDownvotersList=downvotersList;}

}
