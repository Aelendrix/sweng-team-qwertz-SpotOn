package ch.epfl.sweng.spotOn.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  This class represents a photoObject in a form that allows it to be sent to a database.
 *  It is necessary since we can't send raw bitmaps (we convert thumbnail to a string) into the database
 *  It provides a method to convert it to a PhotoObject, which is the only method that should be used
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
    private int mNbReports;
    private List<String> mUpvotersList;
    private List<String> mDownvotersList;
    private List<String> mReportersList;

    // default constructor required to upload object to firebase
    public PhotoObjectStoredInDatabase(){
    }

    /**
     * Constructor meant to be called by the conversion function in the PhotoObject class
     * @param fullSizePhotoLink web url of the full size picture
     * @param thumbnailAsString string containing the information of the tumbnail
     * @param pictureId the unique reference ID of the picture in the firebase DB
     * @param authorID the author unique ID
     * @param photoName the name of the picture
     * @param createdDate the date of creation of the picture
     * @param expireDate the date of expiration of the picture
     * @param latitude the latitude coordinate on the map of when the picture was created
     * @param longitude the longitude coordinate on the map of when the picture was created
     * @param upVotes the amount of upVotes on this picture
     * @param downVotes the amount of downVotes on this picture
     * @param reports the amount users whom reported this picture
     * @param upVotersList the list of users whom upVoted this picture
     * @param downVotersList the list of users whom downVoted this picture
     * @param reportersList the list of users whom reported this picture
     */
    public PhotoObjectStoredInDatabase(String fullSizePhotoLink, String thumbnailAsString,
                                       String pictureId, String authorID, String photoName,
                                       Timestamp createdDate, Timestamp expireDate, double latitude,
                                       double longitude, int upVotes, int downVotes, int reports,
                                       List<String> upVotersList, List<String> downVotersList,
                                       List<String> reportersList){
        mFullSizePhotoLink=fullSizePhotoLink;
        mThumbnailAsString=thumbnailAsString;
        mPictureId=pictureId;
        mAuthorID=authorID;
        mPhotoName=photoName;
        mCreatedDate=createdDate.getTime();
        mExpireDate=expireDate.getTime();
        mLatitude=latitude;
        mLongitude=longitude;
        mNbUpvotes = upVotes;
        mNbDownvotes = downVotes;
        mNbReports = reports;
        mUpvotersList = new ArrayList<>(upVotersList);
        mDownvotersList = new ArrayList<>(downVotersList);
        mReportersList = new ArrayList<>(reportersList);
    }


// PUBLIC METHODS OFFERED BY THIS CLASS

    /** converts the object into a PhotoObject, by converting the thumbnail into a Bitmap
     */
    public PhotoObject convertToPhotoObject(){
        Bitmap thumbnail = convertStringToBitmapImage(mThumbnailAsString);
        List<String> upVotersList;
        List<String> downVotersList;
        List<String> reportersList;

        if(mUpvotersList == null){
            upVotersList = Collections.emptyList();
        }else{
            upVotersList = new ArrayList<>(mUpvotersList);
        }

        if(mDownvotersList == null){
            downVotersList = Collections.emptyList();
        }else{
            downVotersList = new ArrayList<>((mDownvotersList));
        }

        if(mReportersList == null){
            reportersList = Collections.emptyList();
        }
        else{
            reportersList = new ArrayList<>(mReportersList);
        }

        return new PhotoObject(mFullSizePhotoLink, thumbnail, mPictureId, mAuthorID, mPhotoName, mCreatedDate,
                mLatitude, mLongitude, mNbUpvotes, mNbDownvotes, mNbReports, upVotersList, downVotersList,
                reportersList);
    }


    // rather meant to be used for debug
    @Override
    public String toString(){
        String result="PhotoObject";
        result+="   ---   pictureID="+mPictureId;
        result+="   ---   fullSizePhotoLink="+mFullSizePhotoLink;
        result+="   ---   authorID="+mAuthorID;
        result+="   ---   photoName="+mPhotoName;
        result+="   ---   createdDate="+mCreatedDate+"   ---   pos=("+mLatitude+", "+mLongitude+")";
        result+="   ---   upvotes="+mNbUpvotes+" downvotes="+mNbDownvotes;
        result+="   ---   voters are:"+mDownvotersList.toString()+mUpvotersList.toString();
        result+="   ---   reporters are:"+mReportersList.toString();
        result+="   ---   thumbnailAsString length="+mThumbnailAsString.length();
        return result;
    }


// PRIVATE METHODS FOR USE IN THE CLASS ONLY

    /**
     * convert a String to a BitMap
     * @param s the string to be converted
     * @return the bitmap decoded from the string
     */
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
    public int getReports(){ return mNbReports;}
    public List<String> getUpvotersList(){return mUpvotersList;}
    public List<String> getDownvotersList(){return mDownvotersList;}
    public List<String> getReportersList(){return mReportersList;}

    // SETTER REQUIRED (PUBLIC) BY FIREBASE

    public void setFullSizePhotoLink(String fullSizePhotoLink){ mFullSizePhotoLink=fullSizePhotoLink;}
    public void setThumbnailAsString(String thumbnailAsString){mThumbnailAsString=thumbnailAsString;}
    public void setPictureId(String pictureId){mPictureId=pictureId;}
    public void setAuthorID(String authorID){mAuthorID=authorID;}
    public void setPhotoName(String photoName){mPhotoName=photoName;}
    public void setCreatedDate(long createdDate) { mCreatedDate=createdDate; }
    public void setExpireDate(long expireDate) { mExpireDate=expireDate; }
    public void setLatitude(double latitude){mLatitude=latitude;}
    public void setLongitude(double longitude){mLongitude=longitude;}
    public void setUpvotes(int upvotes){mNbUpvotes=upvotes;}
    public void setDownvotes(int downvotes){mNbDownvotes=downvotes;}
    public void setReports(int reports){ mNbReports = reports;}
    public void setUpvotersList(List<String> upvotersList){mUpvotersList=upvotersList;}
    public void setDownvotersList(List<String> downvotersList){mDownvotersList=downvotersList;}
    public void setReportersList(List<String> reportersList){mReportersList=reportersList;}

}
