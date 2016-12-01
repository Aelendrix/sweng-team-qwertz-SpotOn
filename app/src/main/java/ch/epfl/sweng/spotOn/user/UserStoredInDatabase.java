package ch.epfl.sweng.spotOn.user;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
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

// re-added when merging master, should not be needed anymore
//    /* Method to check if the user is already defined in the database and if not it creates it */
//    private void checkUser(){
//        DatabaseReference DBRef = DatabaseRef.getUsersDirectory();
//        Query userQuery = DBRef.orderByChild("userId").equalTo(mUserId);
//
//        ValueEventListener userListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(mUserId == null)
//                {
//                    throw new IllegalArgumentException("UserStoredInDB in User.mUserId is null");
//                }
//                else {
//                    DataSnapshot userToRetrieve = dataSnapshot.child(mUserId);
//                    if (!userToRetrieve.exists()) {
//                        createUserInDB();
//                    } else {
//                        UserStoredInDatabase retrievedUser = userToRetrieve.getValue(UserStoredInDatabase.class);
//
//                        if (retrievedUser == null) {
//                            throw new IllegalStateException("UserStoredInDatabase retrievedUser is null");
//                        } else {
//                            // We can set the fields of User
//                            mKarma = retrievedUser.getKarma();
//                            mPhotosTaken =  ((HashMap<String, Long>) userToRetrieve.child("photosTaken").getValue());
//
//                            if(mPhotosTaken == null){
//                                User.getInstance().setPhotosTaken(new HashMap<String, Long>());
//                            }
//                            else {
//                                User.getInstance().setPhotosTaken(mPhotosTaken);
//                            }
//                            User.getInstance().setKarma(mKarma);
//                            User.getInstance().setIsRetrievedFromDB(true);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                //
//                Log.e("Firebase", "error in checkUser", databaseError.toException());
//            }
//        };
//
//        userQuery.addListenerForSingleValueEvent(userListener);
//    }


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
