package ch.epfl.sweng.spotOn.user;

import com.google.firebase.database.DatabaseReference;

import java.util.Map;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

/*
 * This class corresponds to a UserManager
 * It contains methods to create the user in the database and get the user from the database
 */

public class UserStoredInDatabase {

    private String mFirstName;
    private String mLastName;
    private String mUserId;
    private long mKarma;
    private Map<String, Long> mPhotosTaken;


    public UserStoredInDatabase(){} // needed for use of firebase database


    public UserStoredInDatabase(User user){
        mFirstName = user.getFirstName();
        mLastName = user.getLastName();
        mUserId = user.getUserId();
        mKarma = user.getKarma();
        mPhotosTaken = user.getPhotosTaken();
    }


    /* Add a new user in the database with its karma instantiated to a arbitrary value*/
    public void upload(){
        DatabaseReference DBRef = DatabaseRef.getUsersDirectory();
        DBRef.child(mUserId).setValue(this);
    }


    //PUBLIC GETTERS
    public String getFirstName(){ return mFirstName; }
    public String getLastName(){ return mLastName; }
    public String getUserId(){ return mUserId; }
    public long getKarma() { return mKarma; }
    public Map<String, Long> getPhotosTaken(){ return mPhotosTaken; }


    //PUBLIC SETTERS
    public void setFirstName(String firstName){ mFirstName = firstName; }
    public void setLastName(String lastName){ mLastName = lastName; }
    public void setUserId(String userId){ mUserId = userId; }
    public void setKarma(long karma){ mKarma = karma; }
    public void setPhotosTaken(Map<String, Long> photosTaken) { mPhotosTaken = photosTaken; }
}
