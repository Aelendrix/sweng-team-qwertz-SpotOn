package ch.epfl.sweng.spotOn.user;

/**
 * Created by quentin on 24.11.16.
 */

public interface User {

    int INITIAL_KARMA = 100;
    long MIN_POST_PER_DAY = 1;
    long MAX_POST_PER_DAY = 10;


// PUBLIC METHODS
    boolean isLoggedIn();
    void removeManager();
    void decrementRemainingphotos();


//PUBLIC GETTERS
    String getFirstName();
    String getLastName();
    String getUserId();
    long getKarma();
    long getRemainingPhotos();


//PUBLIC SETTERS
    void setKarma(long karma);
    void setRemainingPhotos(long remainingPhotos) ;
    void setIsRetrievedFromDB(boolean retrievedFromDB);

}
