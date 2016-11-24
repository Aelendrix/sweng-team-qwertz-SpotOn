package ch.epfl.sweng.spotOn.user;

import java.util.Map;

import ch.epfl.sweng.spotOn.media.PhotoObject;

/**
 * Created by quentin on 24.11.16.
 */

public class EmptyUser implements User {

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void removeManager() {
        unsupported();
    }


    @Override
    public long computeRemainingPhotos() {
        return 0;
    }
    @Override
    public void addPhoto(PhotoObject photo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFirstName() {
        throw new UnsupportedOperationException();
    }
    @Override
    public String getLastName() {
        throw new UnsupportedOperationException();
    }
    @Override
    public String getUserId() {
        throw new UnsupportedOperationException();
    }
    @Override
    public long getKarma() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Long> getPhotosTaken() {
        return null;
    }

    @Override
    public boolean getIsRetrievedFromDB() {
        return false;
    }

    @Override
    public void setKarma(long karma) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setIsRetrievedFromDB(boolean retrievedFromDB) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPhotosTaken(Map<String, Long> m) {
        throw new UnsupportedOperationException();
    }


    // PRIVATE HELPERS
    private void unsupported(){
        throw new UnsupportedOperationException("EmptyUser");
    }
}
