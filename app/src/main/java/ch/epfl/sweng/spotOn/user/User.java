package ch.epfl.sweng.spotOn.user;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * User class as a singleton so we have only one instance of this object
 */

public class User {
    private static User mInstance = null;

    public static final int INITIAL_KARMA = 100;
    private static final long MIN_POST_PER_DAY = 1;
    private static final long MAX_POST_PER_DAY = 10;

    private String mFirstName;
    private String mLastName;
    private String mUserId;
    private long mKarma;
    private long mRemainingPhotos;

    private static boolean mIsRetrievedFromDB;

    private List<UserListener> listeners;
    private final static int USER_CONNECTED = 1;
    private final static int USER_DISCONNECTED = 0;

    private User(String firstName, String lastName, String userId){
        mFirstName = firstName;
        mLastName = lastName;
        mUserId = userId;
        mKarma = INITIAL_KARMA;
        mRemainingPhotos = computeMaxPhotoInDay(mKarma);
        mIsRetrievedFromDB = false;
        listeners = new ArrayList<>();
    }

    public static void destroy(){
        mInstance = null;
        mInstance.notifyListeners(USER_DISCONNECTED);
    }

    public static boolean instanceExists(){
        return mInstance!=null;
    }

    public static User getInstance(){
        if(mInstance == null) {
            throw new IllegalStateException("User not initialized");
        } else {
            return mInstance;
        }
    }


    // constructor used from MainActivity during the login phase
    public static void initializeFromFb(String firstName, String lastName, String userId) {
        if(mInstance == null){
            mInstance = new User(firstName,lastName, userId);
            mInstance.getUserAttributesFromDB();
            mInstance.notifyListeners(USER_CONNECTED);
        }else{
            Log.e("User","someone tried to create a new user, but an instance already exists");
        }
    }


    public void getUserAttributesFromDB() {
        mIsRetrievedFromDB = false;
        UserStoredInDatabase userInDB = new UserStoredInDatabase(this);
    }


    public static long computeMaxPhotoInDay(long karma){
        int computed = Math.round((float)Math.sqrt(karma)/10);
        return Math.min(Math.max(computed, MIN_POST_PER_DAY), MAX_POST_PER_DAY);
    }

    public static boolean hasInstance(){
        return mInstance != null;
    }



// LISTENER - RELATED METHODS
    private void notifyListeners(int userState){
        switch(userState){
            case USER_CONNECTED : {
                for (UserListener l : listeners) {
                    l.userConnected();
                }
            }
            case USER_DISCONNECTED : {
                for (UserListener l : listeners) {
                    l.userDisconnected();
                }
            }
            default : throw new IllegalArgumentException("wrong user state");
        }
    }




//PUBLIC GETTERS
    public String getFirstName(){ return mFirstName; }
    public String getLastName(){ return mLastName; }
    public String getUserId(){ return mUserId; }
    public long getKarma() { return mKarma; }
    public long getRemainingPhotos() { return mRemainingPhotos; }
    public boolean getIsRetrievedFromDB() { return mIsRetrievedFromDB; }


//PUBLIC SETTERS
    public void setKarma(long karma){ mKarma = karma; }
    public void setRemainingPhotos(long remainingPhotos) { mRemainingPhotos = remainingPhotos; }
    public void setIsRetrievedFromDB(boolean retrievedFromDB) {
        mIsRetrievedFromDB = retrievedFromDB;
        mInstance.notifyListeners(USER_CONNECTED);
    }
}