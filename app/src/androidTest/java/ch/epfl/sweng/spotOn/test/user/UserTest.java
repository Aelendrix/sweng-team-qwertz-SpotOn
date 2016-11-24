package ch.epfl.sweng.spotOn.test.user;

import android.support.test.runner.AndroidJUnit4;


import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    User testUser;

    @Test
    public void testSetAndGetUser(){
        UserManager.initialize();

        UserManager.getInstance().setUserFromFacebook("firstname", "lastname", "mlb");
        testUser = UserManager.getInstance().getUser();
        testUser.setKarma(500);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.err.print(e);
        }

        Assert.assertEquals(testUser.getFirstName(), "firstname");
        Assert.assertEquals(testUser.getLastName(), "lastname");
        Assert.assertEquals(testUser.getUserId(), "mlb");
        Assert.assertEquals(testUser.getKarma(), 500);
        Assert.assertEquals(testUser.getIsRetrievedFromDB(), false);
        Assert.assertEquals(UserManager.instanceExists(), true);
    }

    @After
    public void removeTestUser() throws Exception{
        DatabaseRef.deleteUserFromDB(testUser.getUserId());
        UserManager.getInstance().destroyUser();

        if(UserManager.instanceExists()) {
            throw new AssertionError(" UserManager should be destroyed");
        }
    }
}
