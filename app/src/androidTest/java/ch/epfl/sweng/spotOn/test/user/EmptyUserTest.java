package ch.epfl.sweng.spotOn.test.user;

import org.junit.Test;

import java.util.HashMap;

import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;
import ch.epfl.sweng.spotOn.user.EmptyUser;

/**
 * Created by quentin on 26.11.16.
 */

public class EmptyUserTest {

    private EmptyUser emptyUser = new EmptyUser();

    @Test
    public void isLoggedInTest(){
        if(emptyUser.isLoggedIn()){
            throw new AssertionError();
        }
    }

    @Test
    public void retrievedFromDBTest(){
        if(!emptyUser.getIsRetrievedFromDB()){
            throw new AssertionError();
        }
    }

    @Test(expected=UnsupportedOperationException.class)
    public void computeRemainingPhotosTest(){
        emptyUser.computeRemainingPhotos();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void addPhotoTest(){
        emptyUser.addPhoto(PhotoObjectTestUtils.getRandomPhotoObject());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void removePhotoTest(){
        emptyUser.removePhoto("pictureIdOfThePhoto");
    }

    @Test(expected=UnsupportedOperationException.class)
    public void getFirstNameTest(){
        emptyUser.getFirstName();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void getLastNameTest(){
        emptyUser.getLastName();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void getUserIdTest(){
        emptyUser.getUserId();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void karmaTest(){
        emptyUser.getKarma();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void setKarmaTest(){
        emptyUser.setKarma(1235);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void setIsRetrievedFromDBTest(){
        emptyUser.setIsRetrievedFromDB(true);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void setPhotoTakenTest(){
        emptyUser.setPhotosTaken(new HashMap<String, Long>());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void getPhotoTakenTest(){
        emptyUser.getPhotosTaken();
    }
}
