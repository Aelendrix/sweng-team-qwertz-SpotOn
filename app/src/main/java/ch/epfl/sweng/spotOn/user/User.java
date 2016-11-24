package ch.epfl.sweng.spotOn.user;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

/**
 * User class as a singleton so we have only one instance of this object
 */

public class User {
    private static User mInstance = null;

    public static final int INITIAL_KARMA = 100;
    private static final long MIN_POST_PER_DAY = 1;
    private static final long MAX_POST_PER_DAY = 10;
    private final long ONE_DAY = 24 * 60 * 60 * 1000;
    private String mFirstName;
    private String mLastName;
    private String mUserId;
    private Map<String, Long> mPhotosTaken;
    private long mKarma;

    private static boolean mIsRetrievedFromDB;

    private User(String firstName, String lastName, String userId){
        mFirstName = firstName;
        mLastName = lastName;
        mUserId = userId;
        mKarma = INITIAL_KARMA;
        mPhotosTaken = new HashMap<>();
        mIsRetrievedFromDB = false;
    }

    public void destroy(){
        mInstance = null;
    }

    public static User getInstance(){
        if(mInstance == null)
        {
            throw new IllegalStateException("User not initialized");
        }
        else {
            return mInstance;
        }
    }


    // constructor used from MainActivity during the login phase
    public static void initializeFromFb(String firstName, String lastName, String userId) {
        if(mInstance == null){
            mInstance = new User(firstName,lastName, userId);
            mInstance.getUserAttributesFromDB();
        }
        else{
            Log.e("User","someone tried to create a new user, but an instance already exists");
        }
    }


    public void getUserAttributesFromDB() {
        mIsRetrievedFromDB = false;
        UserStoredInDatabase userInDB = new UserStoredInDatabase(this);
    }

    public long computeRemainingPhotos(){
        long maxPhotos = computeMaxPhotoInDay();
        updatePhotosTaken();
        long lastPhotosTaken = 0;
        if(mPhotosTaken != null){
            lastPhotosTaken = mPhotosTaken.size();
        }
        return maxPhotos - lastPhotosTaken;
    }


    public long computeMaxPhotoInDay(){
        int computed = Math.round((float)Math.sqrt(mKarma)/10);
        return Math.min(Math.max(computed, MIN_POST_PER_DAY), MAX_POST_PER_DAY);
    }

    public void updatePhotosTaken(){
        long limitTime = System.currentTimeMillis() - ONE_DAY;
        if(mPhotosTaken != null) {
            Set<String> ids = mPhotosTaken.keySet();
            List<String> toRemoveIds = new ArrayList<>();
            for (String id : ids) {
                if (mPhotosTaken.get(id) < limitTime) {
                   toRemoveIds.add(id);
                }
            }
            for (String id : toRemoveIds){
                mPhotosTaken.remove(id);
            }
        }
    }

    public void addPhoto(PhotoObject photo){
        Long currentTime = photo.getCreatedDate().getTime();
        if(mPhotosTaken != null) {
            mPhotosTaken.put(photo.getPictureId(), currentTime);
        }
        DatabaseRef.getUsersDirectory().child(mUserId).child("photosTaken").setValue(mPhotosTaken);
    }

    public static boolean hasInstance(){
        return mInstance != null;
    }



    //PUBLIC GETTERS
    public String getFirstName(){ return mFirstName; }
    public String getLastName(){ return mLastName; }
    public String getUserId(){ return mUserId; }
    public Map<String, Long> getPhotosTaken() { return mPhotosTaken; }
    public long getKarma() { return mKarma; }
    public boolean getIsRetrievedFromDB() { return mIsRetrievedFromDB; }


    //PUBLIC SETTERS
    public void setPhotosTaken(Map<String, Long> photosTaken){ mPhotosTaken = photosTaken;}
    public void setKarma(long karma){ mKarma = karma; }
    public void setIsRetrievedFromDB(boolean retrievedFromDB) {
        mIsRetrievedFromDB = retrievedFromDB;
    }
}