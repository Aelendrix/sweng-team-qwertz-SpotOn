package ch.epfl.sweng.spotOn.user;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

/*
 * This class corresponds to a User
 * It contains methods to create the user in the database and get the user from the database
 */

public class UserStoredInDatabase {

    private String mFirstName;
    private String mLastName;
    private String mUserId;
    private long mKarma;
    private long mRemainingPhotos;


    public UserStoredInDatabase(){} // needed for use of firebase database


    public UserStoredInDatabase(User user){
        mFirstName = user.getFirstName();
        mLastName = user.getLastName();
        mUserId = user.getUserId();
        mKarma = user.getKarma();
        mRemainingPhotos = user.getRemainingPhotos();

        checkUser();
    }


    /* Add a new user in the database with its karma instantiated to a arbitrary value*/
    private void createUserInDB(){
        DatabaseReference DBRef = DatabaseRef.getUsersDirectory();
        DBRef.child(mUserId).setValue(this);
    }


    /* Method to check if the user is already defined in the database and if not it creates it */
    private void checkUser(){
        DatabaseReference DBRef = DatabaseRef.getUsersDirectory();
        Query userQuery = DBRef.orderByChild("userId").equalTo(mUserId);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mUserId == null)
                {
                    throw new IllegalArgumentException("UserStoredInDB in User.mUserId is null");
                }
                else {
                    DataSnapshot userToRetrieve = dataSnapshot.child(mUserId);
                    if (!userToRetrieve.exists()) {
                        createUserInDB();
                    } else {
                        UserStoredInDatabase retrievedUser = userToRetrieve.getValue(UserStoredInDatabase.class);

                        if (retrievedUser == null) {
                            throw new IllegalStateException("UserStoredInDatabase retrievedUser is null");
                        } else {
                            // We can set the fields of User
                            mKarma = retrievedUser.getKarma();
                            mRemainingPhotos = retrievedUser.getRemainingPhotos();

                            User.getInstance().setKarma(mKarma);
                            User.getInstance().setRemainingPhotos(mRemainingPhotos);
                            User.getInstance().setIsRetrievedFromDB(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
                Log.e("Firebase", "error in checkUser", databaseError.toException());
            }
        };

        userQuery.addListenerForSingleValueEvent(userListener);
    }


    //PUBLIC GETTERS
    public String getFirstName(){ return mFirstName; }
    public String getLastName(){ return mLastName; }
    public String getUserId(){ return mUserId; }
    public long getKarma() { return mKarma; }
    public long getRemainingPhotos() { return mRemainingPhotos; }


    //PUBLIC SETTERS
    public void setFirstName(String firstName){ mFirstName = firstName; }
    public void setLastName(String lastName){ mLastName = lastName; }
    public void setUserId(String userId){ mUserId = userId; }
    public void setKarma(long karma){ mKarma = karma; }
    public void setRemainingPhotos(long remainingPhotos) { mRemainingPhotos = remainingPhotos; }
}
