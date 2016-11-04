package ch.epfl.sweng.spotOn.test.user;

import android.support.test.runner.AndroidJUnit4;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.user.User;


@RunWith(AndroidJUnit4.class)
public class UserTest {

    private User testUser;

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


}
