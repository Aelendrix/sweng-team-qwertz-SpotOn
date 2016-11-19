package ch.epfl.sweng.spotOn.user;

/**
 *
 */

public class User {
    private static User mInstance = null;

    public static final int INITIAL_KARMA = 100;
    private static final long MIN_POST_PER_DAY = 1;
    private static final long MAX_POST_PER_DAY = 10;

    private String mFirstName;
    private String mLastName;
    private String mUserId;
    private long mKarma;
    private long mRemainingPhotos;

    private User(){

    }

    public static User getInstance(){
        if(mInstance == null)
        {
            mInstance = new User();
        }
        return mInstance;
    }


    // constructor used from MainActivity during the login phase
    public void setUserAttributesFromFb(String firstName, String lastName, String userId) {

        mFirstName = firstName;
        mLastName = lastName;
        mUserId = userId;
        mKarma = INITIAL_KARMA;
        mRemainingPhotos = computeMaxPhotoInDay(mKarma);

        this.getUserAttributesFromDB();
    }


    public void getUserAttributesFromDB() {
        UserStoredInDatabase userInDB = new UserStoredInDatabase(this);
    }


    public static long computeMaxPhotoInDay(long karma){
        int computed = Math.round((float)Math.sqrt(karma)/10);
        return Math.min(Math.max(computed, MIN_POST_PER_DAY), MAX_POST_PER_DAY);
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