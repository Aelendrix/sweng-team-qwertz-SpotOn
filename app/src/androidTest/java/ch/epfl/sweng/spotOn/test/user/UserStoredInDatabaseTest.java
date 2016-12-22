package ch.epfl.sweng.spotOn.test.user;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.user.UserStoredInDatabase;

@RunWith(AndroidJUnit4.class)
public class UserStoredInDatabaseTest {

    UserStoredInDatabase userInDB;

    @Test
    public void testGetUserStoredInDB(){
        if(UserManager.instanceExists()){
            UserManager.getInstance().destroyUser();
        }else {
            UserManager.initialize();
        }
        UserManager.getInstance().setUserFromFacebook("firstName", "lastName", "mlb");
        User user = UserManager.getInstance().getUser();
        userInDB = new UserStoredInDatabase(user);

        Assert.assertEquals(userInDB.getFirstName(), "firstName");
        Assert.assertEquals(userInDB.getLastName(), "lastName");
        Assert.assertEquals(userInDB.getUserId(), "mlb");
        Assert.assertEquals(userInDB.getKarma(), User.INITIAL_KARMA);

        DatabaseRef.deleteUserFromDB(user.getUserId());
        UserManager.getInstance().destroyUser();
    }

    @Test
    public void testSetUserStoredInDB(){
        if(UserManager.instanceExists()){
            UserManager.getInstance().destroyUser();
        }else {
            UserManager.initialize();
        }
        UserManager.getInstance().setUserFromFacebook("firstName", "lastName", "mlb");
        User user = UserManager.getInstance().getUser();
        userInDB = new UserStoredInDatabase(user);

        userInDB.setFirstName("blabla");
        userInDB.setLastName("test");
        userInDB.setUserId("mlb_test");
        userInDB.setKarma(12);

        Map<String, Long> mapPhotoTaken = new HashMap<String, Long>() {};

        Long testLong = 12L;
        mapPhotoTaken.put("test",testLong);
        userInDB.setPhotosTaken(mapPhotoTaken);

        Assert.assertEquals(userInDB.getFirstName(), "blabla");
        Assert.assertEquals(userInDB.getLastName(), "test");
        Assert.assertEquals(userInDB.getUserId(), "mlb_test");
        Assert.assertEquals(userInDB.getKarma(), 12);
        Assert.assertEquals(userInDB.getPhotosTaken().equals(mapPhotoTaken), true);

        DatabaseRef.deleteUserFromDB(user.getUserId());
        UserManager.getInstance().destroyUser();
    }

    @Test
    public void testUploadUserStoredInDB(){
        UserManager.initialize();
        UserManager.getInstance().setUserFromFacebook("first", "last", "ml");
        User user = UserManager.getInstance().getUser();
        userInDB = new UserStoredInDatabase(user);

        userInDB.upload();

        Assert.assertEquals(userInDB.getKarma(), User.INITIAL_KARMA);

        DatabaseRef.deleteUserFromDB(user.getUserId());
        UserManager.getInstance().destroyUser();
    }
}
