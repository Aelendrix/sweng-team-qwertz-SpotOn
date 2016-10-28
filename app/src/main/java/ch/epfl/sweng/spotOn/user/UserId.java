package ch.epfl.sweng.spotOn.user;


/*
 * Singleton class used to store a global variable: the userId
 * We need to be able to access it from anywhere in the app
 */
public class UserId {
    private static UserId mInstance = null;

    private String mUserId;

    private UserId(){

    }

    public static UserId getInstance(){
        if(mInstance == null)
        {
            mInstance = new UserId();
        }
        return mInstance;
    }

    public String getUserId(){
        return mUserId;
    }

    public void setUserId(String userId){
        mUserId = userId;
    }
}
