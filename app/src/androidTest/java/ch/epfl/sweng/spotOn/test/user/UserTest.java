package ch.epfl.sweng.spotOn.test.user;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.Date;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.user.RealUser;
import ch.epfl.sweng.spotOn.user.User;

import static ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils.getBitmapFromURL;

@RunWith(AndroidJUnit4.class)
public class UserTest {

    private User testUser = null;

    @Test
    public void testSetAndGetUser() throws InterruptedException{
        testUser = new RealUser("firstName","lastName","mlb",null);
        testUser.setKarma(500);

        Assert.assertEquals(testUser.getFirstName(), "firstName");
        Assert.assertEquals(testUser.getLastName(), "lastName");
        Assert.assertEquals(testUser.getUserId(), "mlb");
        Assert.assertEquals(testUser.getKarma(), 500);
        Assert.assertEquals(testUser.computeRemainingPhotos(), RealUser.computeMaxPhotoInDay(500));
        Assert.assertEquals(testUser.isLoggedIn(), false);
        Assert.assertEquals(testUser.getIsRetrievedFromDB(), false);

        DatabaseRef.deleteUserFromDB(testUser.getUserId());
    }


    @Test
    public void testAddAndRemovePhoto() throws InterruptedException{
        testUser = new RealUser("firstName","lastName","mlb",null);

        Bitmap image = getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Germain_Derycke_%281954%29.jpg/450px-Germain_Derycke_%281954%29.jpg");
        PhotoObject po = new PhotoObject(image, testUser.getUserId(), "photo1", new Timestamp(new Date().getTime()), 46.52890355757888, 6.569420238493888);

        testUser.addPhoto(po);

        Assert.assertEquals(testUser.getPhotosTaken().containsKey(po.getPictureId()), true);
        Assert.assertEquals(testUser.retrieveUpdatedPhotosTaken().containsKey(po.getPictureId()), true);

        testUser.removePhoto(po.getPictureId());

        Assert.assertEquals(testUser.getPhotosTaken().isEmpty(), true);
        Assert.assertEquals(testUser.retrieveUpdatedPhotosTaken().isEmpty(), true);

        DatabaseRef.deleteUserFromDB(testUser.getUserId());
    }
}

