package ch.epfl.sweng.spotOn.test.user;


import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserStoredInDatabase;

@RunWith(AndroidJUnit4.class)
public class UserStoredInDatabaseTest {

    UserStoredInDatabase userInDB = null;

    @Test
    public void testGetUserStoredInDB(){
        User.initializeFromFb("firstname", "lastname", "mlb");
        User user = User.getInstance();
        userInDB = new UserStoredInDatabase(user);

        Assert.assertEquals(userInDB.getFirstName(), "firstname");
        Assert.assertEquals(userInDB.getLastName(), "lastname");
        Assert.assertEquals(userInDB.getUserId(), "mlb");
        Assert.assertEquals(userInDB.getKarma(), User.INITIAL_KARMA);
        Assert.assertEquals(userInDB.getRemainingPhotos(), User.computeMaxPhotoInDay(User.INITIAL_KARMA));

        DatabaseRef.deleteUserFromDB(user.getUserId());
        user.destroy();
    }

    @Test
    public void testSetUserStoredInDB(){
        User.initializeFromFb("firstname", "lastname", "mlb");
        User user = User.getInstance();
        userInDB = new UserStoredInDatabase(user);
        userInDB.setFirstName("blabla");
        userInDB.setLastName("test");
        userInDB.setUserId("mlb_test");
        userInDB.setKarma(12);
        userInDB.setRemainingPhotos(10);

        Assert.assertEquals(userInDB.getFirstName(), "blabla");
        Assert.assertEquals(userInDB.getLastName(), "test");
        Assert.assertEquals(userInDB.getUserId(), "mlb_test");
        Assert.assertEquals(userInDB.getKarma(), 12);
        Assert.assertEquals(userInDB.getRemainingPhotos(), 10);

        DatabaseRef.deleteUserFromDB(user.getUserId());
        user.destroy();
    }
}
