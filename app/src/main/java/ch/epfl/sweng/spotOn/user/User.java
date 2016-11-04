package ch.epfl.sweng.spotOn.user;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/*
 * This class corresponds to a User
 * It contains methods to create the user in the database and get the user from the database
 */

public class User {

    private final String DATABASE_USERS_PATH = "UsersDirectory"; // used for Database Reference

    private String mFirstName;
    private String mLastName;
    private String mUserId;


    public User(String userId){
        mUserId = userId;

        getUser();
    }


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
        Query userQuery = DBRef.orderByChild("userId");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExists = false;
                // test if user is already in the db
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String key = userSnapshot.getKey();
                    if(mUserId.equals(key)){
                        userExists = true;
                        retrieveUserFirstName(updateFirstNameListener(),failureFirstNameListener());
                        //working only in debug mode
                        //mFirstName = userSnapshot.child("firstName").getValue().toString();
                        //mLastName = userSnapshot.child("lastName").getValue().toString();
                        break;
                    }
                }

                if(!userExists){
                    createUserInDB();
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

    public void retrieveUserFirstName(final OnSuccessListener customOnSuccessListener,
                                      OnFailureListener customOnFailureListener) {
        DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference(DATABASE_USERS_PATH);
        Query userQuery = DBRef.orderByChild("userId");

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExists = false;
                // test if user is already in the db
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    String key = userSnapshot.getKey();
                    if(mUserId.equals(key)){
                        userExists = true;
                        // try to get the firstname in the db
                        Task<Object> firstNameTask = (Task) userSnapshot.child("firstName").getValue();
                        firstNameTask.addOnSuccessListener(customOnSuccessListener);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //
                Log.e("Firebase", "error in checkUser", databaseError.toException());
            }
        };
    }


    private OnSuccessListener updateFirstNameListener(){
        return new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String firstName) {
                mFirstName = firstName;
            }
        };
    }


    private OnFailureListener failureFirstNameListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FirstNameFailure", e.toString());
                mFirstName = "";
            }
        };
    }

    //PUBLIC GETTERS
    public String getFirstName(){ return mFirstName; }
    public String getLastName(){ return mLastName; }
    public String getUserId(){ return mUserId; }

    //PUBLIC SETTERS
    public void setFirstName(String firstName){ mFirstName = firstName; }
    public void setLastName(String lastName){ mLastName = lastName; }
    public void setUserId(String userId){ mUserId = userId; }

}
