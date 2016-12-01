package ch.epfl.sweng.spotOn.user;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

public class RealUser implements User {

    private String mFirstName;
    private String mLastName;
    private String mUserId;
    private long mKarma;
    private Map<String, Long> mPhotosTaken;

    private static boolean mIsRetrievedFromDB;

    private UserManager mManager;




// CONSTRUCTOR
    public RealUser(String firstName, String lastName, String userId, UserManager manager){
        mFirstName = firstName;
        mLastName = lastName;
        mUserId = userId;
        mKarma = User.INITIAL_KARMA;
        mPhotosTaken = new HashMap<>();
        mIsRetrievedFromDB = false;
        mManager = manager;
        lookUpUserInDatabase();
    }



// PUBLIC METHODS
    public void getUserAttributesFromDB() {
        mIsRetrievedFromDB = false;
        // will trigger the refresh from database
        UserStoredInDatabase userInDB = new UserStoredInDatabase(this);
    }

    public boolean isLoggedIn(){
        return mIsRetrievedFromDB;
    }

    public void removeManager(){
        mManager = null;
    }

    public long computeRemainingPhotos(){
        long maxPhotos = computeMaxPhotoInDay(mKarma);
        updatePhotosTaken();
        long lastPhotosTaken = 0;
        if(mPhotosTaken != null){
            lastPhotosTaken = mPhotosTaken.size();
        }
        return maxPhotos - lastPhotosTaken;
    }

    private long computeMaxPhotoInDay(long karma){
        int computed = Math.round((float)Math.sqrt(karma)/10);
        return Math.min(Math.max(computed, MIN_POST_PER_DAY), MAX_POST_PER_DAY);
    }

    public void addPhoto(PhotoObject photo){
        Long currentTime = photo.getCreatedDate().getTime();
        if(mPhotosTaken != null) {
            mPhotosTaken.put(photo.getPictureId(), currentTime);
        }
        DatabaseRef.getUsersDirectory().child(mUserId).child("photosTaken").setValue(mPhotosTaken);
    }

    public void removePhoto(String pictureID){
        mPhotosTaken.remove(pictureID);
        DatabaseRef.getUsersDirectory().child(mUserId).child("photosTaken").setValue(mPhotosTaken);
    }



//PUBLIC GETTERS
    public String getFirstName(){ return mFirstName; }
    public String getLastName(){ return mLastName; }
    public String getUserId(){ return mUserId; }
    public long getKarma() { return mKarma; }
    public Map<String, Long> getPhotosTaken() {
        return mPhotosTaken;
    }
    public boolean getIsRetrievedFromDB() { return mIsRetrievedFromDB; }




//PUBLIC SETTERS
    public void setKarma(long karma){ mKarma = karma; }
    public void setPhotosTaken(Map<String, Long> m) {
        mPhotosTaken = m;
    }
    public void setIsRetrievedFromDB(boolean retrievedFromDB) {
        if(mManager!=null) {
            mIsRetrievedFromDB = retrievedFromDB;
            mManager.notifyListeners(UserManager.USER_CONNECTED);
        }
    }




// PRIVATE HELPERS

    private void updatePhotosTaken(){
        long limitTime = System.currentTimeMillis() - User.ONE_DAY;
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

    /* Method to check if the user is already defined in the database and if not it creates it */
    private void lookUpUserInDatabase(){
        final RealUser refToThis = this;
        DatabaseReference DBRef = DatabaseRef.getUsersDirectory();
        Query userQuery = DBRef.orderByChild("userId").equalTo(mUserId);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mUserId == null){
                    throw new IllegalArgumentException("UserStoredInDB in UserManager.mUserId is null");
                } else {
                    DataSnapshot userToRetrieve = dataSnapshot.child(mUserId);
                    if (!userToRetrieve.exists()) {
                        UserStoredInDatabase newUser = new UserStoredInDatabase(refToThis);
                        newUser.upload();
                    } else {
                        UserStoredInDatabase retrievedUser = userToRetrieve.getValue(UserStoredInDatabase.class);

                        if (retrievedUser == null) {
                            throw new IllegalStateException("UserStoredInDatabase retrievedUser is null");
                        } else {
                            // We can set the fields
                            mKarma = retrievedUser.getKarma();
                            mPhotosTaken =  ((HashMap<String, Long>) userToRetrieve.child("photosTaken").getValue());

                            if(mPhotosTaken == null){
                                refToThis.setPhotosTaken(new HashMap<String, Long>());
                            }
                            else {
                                refToThis.setPhotosTaken(mPhotosTaken);
                            }
                            refToThis.setKarma(mKarma);
                            refToThis.setIsRetrievedFromDB(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "error in checkUser", databaseError.toException());
            }
        };

        userQuery.addListenerForSingleValueEvent(userListener);
    }

}
