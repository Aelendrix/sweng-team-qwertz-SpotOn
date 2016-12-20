package ch.epfl.sweng.spotOn.user;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * UserManager class as a singleton so we have only one instance of this object
 */

public class UserManager {

    private static UserManager mSingleInstance = null;
    private static User mUser = null;
    private boolean isLogInThroughFacebook = false;

    private List<UserListener> listeners;
    public final static int USER_CONNECTED = 1;
    public final static int USER_DISCONNECTED = 0;


    // INITIALIZE AND CONSTRUCTORS
    public static void initialize(){
        if(mSingleInstance==null) {
            mSingleInstance = new UserManager();
        }
    }
    private UserManager(){
        listeners = new ArrayList<>();
    }



    // PUBLIC METHODS
    public static boolean instanceExists(){
        return mSingleInstance!=null;
    }

    public static UserManager getInstance(){
        if(mSingleInstance == null) {
            throw new IllegalStateException("UserManager not initialized");
        } else {
            return mSingleInstance;
        }
    }

    public boolean userIsLoggedIn() {
        return mUser != null && mUser.isLoggedIn();
    }

    public boolean retrievingUserFromDatabase() {
        return mUser != null && !mUser.getIsRetrievedFromDB();
    }

    public User getUser(){
        if(mUser == null){
            throw new IllegalStateException("User not logged in - check userIsLoggedIn() before calling getUser()");
        }
        return mUser;
    }

    public void destroyUser(){
        if(mUser!=null){
            mUser.removeManager();
            mUser = null;
            notifyListeners(USER_DISCONNECTED);
            isLogInThroughFacebook = false;
        }
    }

    public void addListener(UserListener l){
        listeners.add(l);
    }

    /** Set user - for tests only   */
    public void setMockUser(User u){
        mUser = u;
        notifyListeners(USER_CONNECTED);
    }

    public boolean isLogInThroughFacebook() {
        return isLogInThroughFacebook;
    }

    // SET USERS METHODS
    public void setUserFromFacebook(String firstName, String lastName, String userId) {
        if(mSingleInstance==null){
            throw new IllegalStateException("UserManager should be initialized");
        }
        if(mUser == null || !mUser.isLoggedIn()){
            isLogInThroughFacebook = true;
            RealUser newUser = new RealUser(firstName,lastName, userId, mSingleInstance);
            newUser.getUserAttributesFromDB();
            mUser = newUser;
            // listeners will be notified once the user has retrieved its info from DB
        }else{
            Log.e("UserManager","someone tried to create a new user, but an instance already exists");
        }
    }

    public void setEmptyUser(){
        if(mSingleInstance==null){
            throw new IllegalStateException("UserManager should be initialized");
        }
        if(mUser==null || !mUser.isLoggedIn()){
            isLogInThroughFacebook = false;
            mUser = new EmptyUser();
        }else{
            Log.e("UserManager","someone tried to create a new user, but an instance already exists");
        }
    }







// LISTENER - RELATED METHODS
    public static void notifyListeners(int userState){
        switch(userState){
            case USER_CONNECTED : {
                for (UserListener l : mSingleInstance.listeners) {
                    l.userConnected();
                }
                break;
            }
            case USER_DISCONNECTED : {
                for (UserListener l : mSingleInstance.listeners) {
                    l.userDisconnected();
                }
                break;
            }
            default : throw new IllegalArgumentException("wrong user state argument");
        }
    }





}