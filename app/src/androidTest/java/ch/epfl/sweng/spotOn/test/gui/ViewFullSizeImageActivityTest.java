package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.location.Location;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.ViewFullsizeImageActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Alexis Dewaele on 09/11/2016.
 */

@RunWith(AndroidJUnit4.class)
public class ViewFullSizeImageActivityTest {

    @Rule
    public ActivityTestRule<ViewFullsizeImageActivity> mActivityTestRule = new ActivityTestRule<>(ViewFullsizeImageActivity.class,true,false);
    public String pictureID1;
    public String pictureID2;
    public Intent displayFullSizeImageIntent;

    @Before
    public void getPictureID(){

        Location location = new Location("testLocationProvider");
        location.setLatitude(46.52890355757567);
        location.setLongitude(6.569420238493345);
        location.setAltitude(0);
        location.setTime(System.currentTimeMillis());

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest(location);
        LocalDatabase.initialize(mlt);

        PhotoObject po1 = PhotoObjectTestUtils.paulVanDykPO();
        po1.upload();
        LocalDatabase.getInstance().addPhotoObject(po1);
        pictureID1 = po1.getPictureId();

        displayFullSizeImageIntent = new Intent();
        displayFullSizeImageIntent.putExtra(ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID, pictureID1);

        PhotoObject po2 = PhotoObjectTestUtils.germaynDeryckePO();
        po2.upload();
        LocalDatabase.getInstance().addPhotoObject(po2);
        pictureID2 = po2.getPictureId();
    }

    @Test
    public void launchFullPictureActivity() throws InterruptedException{
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
        Thread.sleep(1000);
        onView(withText("Up !!")).perform(click());
        Thread.sleep(1000);
        onView(withText("Down")).perform(click());
    }

    @Test
    public void swipeBetweenPicturesTest() throws InterruptedException{
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
        Thread.sleep(1000);
        onView(withId(R.id.pager)).perform(swipeLeft());
    }

    @After
    public void deletePhotoObject(){
        DatabaseRef.deletePhotoObjectFromDB(pictureID1);
        DatabaseRef.deletePhotoObjectFromDB(pictureID2);
        StorageRef.deletePictureFromStorage(pictureID1);
        StorageRef.deletePictureFromStorage(pictureID2);
        LocalDatabase.getInstance().removePhotoObject(pictureID1);
        LocalDatabase.getInstance().removePhotoObject(pictureID2);
    }

}
