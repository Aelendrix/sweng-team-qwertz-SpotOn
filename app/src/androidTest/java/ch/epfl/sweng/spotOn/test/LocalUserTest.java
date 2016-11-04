package ch.epfl.sweng.spotOn.test;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.localObjects.LocalUser;
import ch.epfl.sweng.spotOn.user.User;

/**
 * Created by nico on 03.11.16.
 */
@RunWith(AndroidJUnit4.class)
public class LocalUserTest {


    @Test
    public void testLocalUser() throws Exception{
        LocalUser lu = new LocalUser();
        User u = new User("aaa","bbb","123456789");
        lu.setCurrentUser(u);
        User v = lu.getCurrentUser();
        if((!v.getFirstName().equals(u.getFirstName()))
                ||(!v.getLastName().equals(u.getLastName()))
                ||(!v.getUserId().equals(u.getUserId()))){
                    throw new AssertionError("user wrongly set");
        }
    }
}
