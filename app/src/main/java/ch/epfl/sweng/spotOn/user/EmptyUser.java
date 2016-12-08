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
        // nothing
    }


    @Override
    public long computeRemainingPhotos() {
        throw new UnsupportedOperationException();
    }
    @Override
    public void addPhoto(PhotoObject photo) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void removePhoto(String pictureID) { throw  new UnsupportedOperationException(); }

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
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Long> retrieveUpdatedPhotosTaken() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getIsRetrievedFromDB() {
        throw new UnsupportedOperationException();
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

}
