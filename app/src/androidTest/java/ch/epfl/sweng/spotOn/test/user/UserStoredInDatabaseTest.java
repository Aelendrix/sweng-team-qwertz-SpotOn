package ch.epfl.sweng.spotOn.test.user;


import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

        Map<String, Long> mapPhotoTaken = new Map<String, Long>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public Long get(Object key) {
                return null;
            }

            @Override
            public Long put(String key, Long value) {
                return null;
            }

            @Override
            public Long remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map<? extends String, ? extends Long> m) {

            }

            @Override
            public void clear() {

            }

            @NonNull
            @Override
            public Set<String> keySet() {
                return null;
            }

            @NonNull
            @Override
            public Collection<Long> values() {
                return null;
            }

            @NonNull
            @Override
            public Set<Entry<String, Long>> entrySet() {
                return null;
            }
        };

        Long testLong = new Long(12);
        mapPhotoTaken.put("test",testLong);
        userInDB.setPhotosTaken(mapPhotoTaken);

        Assert.assertEquals(userInDB.getFirstName(), "blabla");
        Assert.assertEquals(userInDB.getLastName(), "test");
        Assert.assertEquals(userInDB.getUserId(), "mlb_test");
        Assert.assertEquals(userInDB.getKarma(), 12);
        Assert.assertEquals(userInDB.getPhotosTaken().equals(mapPhotoTaken), true);

        DatabaseRef.deleteUserFromDB(user.getUserId());
        user.destroy();
    }
}
