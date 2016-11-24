package ch.epfl.sweng.spotOn.user;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

public class RealUser implements User {

    private String mFirstName;
    private String mLastName;
    private String mUserId;
    private long mKarma;
    private long mRemainingPhotos;

    private static boolean mIsRetrievedFromDB;

    private UserManager mManager;




// CONSTRUCTOR
    public RealUser(String firstName, String lastName, String userId, UserManager manager){
        mFirstName = firstName;
        mLastName = lastName;
        mUserId = userId;
        mKarma = User.INITIAL_KARMA;
        mRemainingPhotos = computeMaxPhotoInDay(mKarma);
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



// STATIC HELPERS
    public static long computeMaxPhotoInDay(long karma){
        int computed = Math.round((float)Math.sqrt(karma)/10);
        return Math.min(Math.max(computed, User.MIN_POST_PER_DAY), User.MAX_POST_PER_DAY);
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
        if(mManager!=null) {
            mIsRetrievedFromDB = retrievedFromDB;
            mManager.notifyListeners(UserManager.USER_CONNECTED);
        }
    }


// PRIVATE HELPERS

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
                            mRemainingPhotos = retrievedUser.getRemainingPhotos();

                            refToThis.setKarma(mKarma);
                            refToThis.setRemainingPhotos(mRemainingPhotos);
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
