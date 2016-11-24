package ch.epfl.sweng.spotOn.user;

import java.util.Map;

import ch.epfl.sweng.spotOn.media.PhotoObject;

/**
 * Created by quentin on 24.11.16.
 */

public interface User {

    int INITIAL_KARMA = 100;
    long MAX_POST_PER_DAY = 10;
    long MIN_POST_PER_DAY = 1;
    long ONE_DAY = 24 * 60 * 60 * 1000;


// PUBLIC METHODS
    boolean isLoggedIn();
    void removeManager();
    void addPhoto(PhotoObject photo);
    long computeRemainingPhotos();


//PUBLIC GETTERS
    String getFirstName();
    String getLastName();
    String getUserId();
    long getKarma();
    Map<String, Long> getPhotosTaken();
    boolean getIsRetrievedFromDB();


//PUBLIC SETTERS
    void setKarma(long karma);
    void setIsRetrievedFromDB(boolean retrievedFromDB);
    void setPhotosTaken(Map<String, Long> m);

}
