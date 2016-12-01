package ch.epfl.sweng.spotOn.test.user;

import android.support.test.runner.AndroidJUnit4;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.user.User;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    private User testUser = null;

    @Test
    public void testSetAndGetUser(){
        User.initializeFromFb("firstname", "lastname", "mlb");
        testUser = User.getInstance();
        testUser.setKarma(500);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.err.print(e);
        }

        Assert.assertEquals(testUser.getFirstName(), "firstname");
        Assert.assertEquals(testUser.getLastName(), "lastname");
        Assert.assertEquals(testUser.getUserId(), "mlb");
        Assert.assertEquals(testUser.getKarma(), 500);
        Assert.assertEquals(testUser.getIsRetrievedFromDB(), false);
        Assert.assertEquals(User.hasInstance(), true);
    }


    @Test
    public void testComputeRemainingPhotos(){
        User.initializeFromFb("first", "last", "mlb1");
        testUser = User.getInstance();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.err.print(e);
        }

        long remainingPictures = testUser.computeRemainingPhotos();

        Assert.assertEquals(remainingPictures, 1);
    }


    @After
    public void removeTestUser() throws Exception{
        if(User.hasInstance()) {
            DatabaseRef.deleteUserFromDB(testUser.getUserId());
            testUser.destroy();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.err.print(e);
            }

            if(User.hasInstance()) {
                throw new AssertionError(" User should be destroyed");
            }
        }
    }
}
