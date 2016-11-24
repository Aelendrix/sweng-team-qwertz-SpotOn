package ch.epfl.sweng.spotOn.user;

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
    public void decrementRemainingphotos() {
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
    public long getRemainingPhotos() {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setKarma(long karma) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setRemainingPhotos(long remainingPhotos) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void setIsRetrievedFromDB(boolean retrievedFromDB) {
        throw new UnsupportedOperationException();
    }


    // PRIVATE HELPERS
    private void unsupported(){
        throw new UnsupportedOperationException("EmptyUser");
    }
}
