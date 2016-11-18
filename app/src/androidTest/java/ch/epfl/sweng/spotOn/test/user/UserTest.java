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
    private User mlbTest = null;


    @After
    public void removeTestUser(){
        User user1 = new User("mlb","test","12");

        DatabaseRef.deleteUserFromDB(user1.getUserId());
    }


    @Test
    public void setUser(){
        User user1 = new User("mlb","test","12");

        testUser = new User();
        testUser.setFirstName("mlb");
        testUser.setLastName("test");
        testUser.setUserId("12");

        Assert.assertEquals(user1,testUser);
    }


    @Test
    public void getUser(){
        testUser = new User("mlb","test","12");

        Assert.assertEquals(testUser.getFirstName(), "mlb");
        Assert.assertEquals(testUser.getLastName(), "test");
        Assert.assertEquals(testUser.getUserId(), "12");

    }


    // This method tests the User method getUser()
    @Test
    public void testGetUser(){
        UserProfileActivity userProfile = null;
        mlbTest = new User("10209394363510335",userProfile);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.err.print(e);
        }

        Assert.assertEquals(mlbTest.getFirstName(), "Marie-Laure");
        Assert.assertEquals(mlbTest.getLastName(), "Barbier");
        Assert.assertEquals(mlbTest.getUserId(), "10209394363510335");

    }

}
