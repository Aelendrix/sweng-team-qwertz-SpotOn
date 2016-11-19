package ch.epfl.sweng.spotOn.test.user;

import android.support.test.runner.AndroidJUnit4;


import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.gui.UserProfileActivity;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.user.User;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    private User testUser = null;

    @After
    public void removeTestUser() {
        DatabaseRef.deleteUserFromDB(testUser.getUserId());
    }

    // This method tests the User method getUser()
    @Test
    public void testSetAndGetUser(){
        testUser = User.getInstance();
        testUser.setFirstName("firstname");
        testUser.setLastName("lastname");
        testUser.setUserId("12");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.err.print(e);
        }

        Assert.assertEquals(testUser.getFirstName(), "firstname");
        Assert.assertEquals(testUser.getLastName(), "lastname");
        Assert.assertEquals(testUser.getUserId(), "12");
    }

    public void testComputeRemainingPictures(){
        testUser.setKarma(500);
        testUser.setRemainingPhotos(User.computeMaxPhotoInDay(testUser.getKarma()));

        Assert.assertEquals(testUser.getRemainingPhotos(), 5);
    }

}
