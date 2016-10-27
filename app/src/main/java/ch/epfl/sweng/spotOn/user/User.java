package ch.epfl.sweng.spotOn.user;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
 * This class corresponds to a User
 * It contains methods to create the user in the database and get the user from the database
 */

public class User {

    private final String DATABASE_USERS_PATH = "UsersDirectory"; // used for Database Reference

    private String mFirstName;
    private String mLastName;
    private String mUserId;


    public User(){

    }


    public User(String firstName, String lastName, String userId) {

        mFirstName = firstName;
        mLastName = lastName;
        mUserId = userId;

        UserId singletonUserId = UserId.getInstance();
        singletonUserId.setUserId(userId);

        if(userExists() == false){
            createUserInDB();
        }
    }


    /* Add a new user in the database */
    private void createUserInDB(){
        DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference(DATABASE_USERS_PATH);
        DBRef.child(mUserId).setValue(this);
    }


    /* Method to detect if a user is already defined in the database */
    private boolean userExists(){
        boolean userExists = false;
        DatabaseReference DBRef = FirebaseDatabase.getInstance().getReference(DATABASE_USERS_PATH);

        if(DBRef.child(mUserId).getKey() != mUserId){
            userExists = true;
        }

        return userExists;
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
