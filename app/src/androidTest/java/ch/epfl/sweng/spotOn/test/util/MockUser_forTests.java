package ch.epfl.sweng.spotOn.test.util;

import java.util.Map;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;

/**
 * Created by quentin on 29.11.16.
 */

public class MockUser_forTests implements User {

    private String mFirstName;
    private String mLastName;
    private String mUserId;
    private long mKarma;
    private Map<String, Long> mPhotosTaken;

    private boolean mIsLoggedIn;


    public MockUser_forTests(String firstName, String lastName, String uid, long karma, Map<String, Long> taken, boolean isLoggedIn){
        mFirstName=firstName;
        mLastName=lastName;
        mUserId=uid;
        mKarma=karma;
        mPhotosTaken=taken;
        mIsLoggedIn=isLoggedIn;
    }

    @Override
    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }

    @Override
    public void removeManager() {
        // nothing
    }

    @Override
    public void addPhoto(PhotoObject photo) {
        mPhotosTaken.put(photo.getPictureId(), 0l);
    }

    @Override
    public long computeRemainingPhotos() {
        return 0;
    }

    @Override
    public void removePhoto(String pictureID){}

    @Override
    public String getFirstName() {
        return mFirstName;
    }

    @Override
    public String getLastName() {
        return mLastName;
    }

    @Override
    public String getUserId() {
        return mUserId;
    }

    @Override
    public long getKarma() {
        return mKarma;
    }

    @Override
    public Map<String, Long> getPhotosTaken() {
        return mPhotosTaken;
    }

    @Override
    public void setKarma(long karma) {
        mKarma=karma;
    }

    @Override
    public void setIsRetrievedFromDB(boolean retrievedFromDB) {
        mIsLoggedIn=retrievedFromDB;
    }
    public void setLoggedIn(boolean b){
        mIsLoggedIn=b;
    }

    @Override
    public void setPhotosTaken(Map<String, Long> m) {
        mPhotosTaken=m;
    }
}
