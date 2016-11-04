package ch.epfl.sweng.spotOn.user;

import android.util.Log;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.spotOn.gui.UserProfile;
/*
 * This class corresponds to a User
 * It contains methods to create the user in the database and get the user from the database
 */

public class User {

    private static final String DATABASE_USERS_PATH = "UsersDirectory"; // used for Database Reference

    private String mFirstName;
    private String mLastName;
    private String mUserId;


    public User(){} // needed for use of firebase database


    //constructor only used from UserProfile
    public User(String userId, UserProfile userProfile){
        mUserId = userId;

        getUser(userProfile);
    }


    // constructor used from MainActivity during the login phase
    public User(String firstName, String lastName, String userId) {

        mFirstName = firstName;
        mLastName = lastName;
        mUserId = userId;

        UserId singletonUserId = UserId.getInstance();
        singletonUserId.setUserId(userId);

        getUser();
    }


    /* Add a new user in the database */
    private void createUserInDB(){
        DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference(DATABASE_USERS_PATH);
        DBRef.child(mUserId).setValue(this);
    }


    /* Method to get the user if it is already defined in the database and if not it creates it */
    private void getUser(){
        DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference(DATABASE_USERS_PATH);
        Query userQuery = DBRef.orderByChild("userId").equalTo(mUserId);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot userToRetrieve = dataSnapshot.child(mUserId);  // important
                if (!userToRetrieve.exists()) {
                    createUserInDB();
                }
                User retrievedUser = userToRetrieve.getValue(User.class);
                // We can set the fields of User
                mFirstName = retrievedUser.getFirstName();
                mLastName = retrievedUser.getLastName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
                Log.e("Firebase", "error in getUser", databaseError.toException());
            }
        };

        userQuery.addListenerForSingleValueEvent(userListener);

    }


    /* Method to get the user already defined in the database */
    private void getUser(final UserProfile userProfile) {
        DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference(DATABASE_USERS_PATH);
        Query userQuery = DBRef.orderByChild("userId").equalTo(mUserId);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot userToRetrieve = dataSnapshot.child(mUserId);
                if (!userToRetrieve.exists()) {
                    throw new AssertionError("UserId doesn't exist in the database " + mUserId);
                }
                User retrievedUser = userToRetrieve.getValue(User.class);

                mFirstName = retrievedUser.getFirstName();
                mLastName = retrievedUser.getLastName();

                if(userProfile == null) {
                    Log.e("UserError","userProfile is null");
                }
                else {
                    userProfile.fillInFields();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
                Log.e("Firebase", "error in getUser with UserProfile", databaseError.toException());
            }
        };

        userQuery.addListenerForSingleValueEvent(userListener);

    }


    //PUBLIC GETTERS
    public String getFirstName(){ return mFirstName; }
    public String getLastName(){ return mLastName; }
    public String getUserId(){ return mUserId; }


    //PUBLIC SETTERS
    public void setFirstName(String firstName){ mFirstName = firstName; }
    public void setLastName(String lastName){ mLastName = lastName; }
    public void setUserId(String userId){ mUserId = userId; }


    @Override
    public int hashCode() {
        int result = mFirstName.hashCode();
        result = 31 * result + mLastName.hashCode();
        result = 31 * result + mUserId.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!mFirstName.equals(user.mFirstName)) return false;
        if (!mLastName.equals(user.mLastName)) return false;
        return mUserId.equals(user.mUserId);

    }
}
