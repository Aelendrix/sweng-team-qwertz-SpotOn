package ch.epfl.sweng.spotOn.test.gui;


import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.gui.ViewFullsizeImageActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker;
import ch.epfl.sweng.spotOn.test.util.TestPhotoObjectUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by nico on 09.11.16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FullPictureActivityTest {


    @Rule
    public ActivityTestRule<ViewFullsizeImageActivity> mActivityTestRule = new ActivityTestRule<>(ViewFullsizeImageActivity.class,true,false);
    public String pictureID;
    public Intent displayFullsizeImageIntent;

    @Before
    public void initLocalDatabase(){
        // new
        // don't modifie the values passed to MockLocationTracker
        MockLocationTracker mlt = new MockLocationTracker(46.52890355757567, 6.569420238493345);
        LocalDatabase.initialize(mlt);
// old
//        Location location = new Location("testLocationProvider");
//        location.setLatitude(46.52890355757567);
//        location.setLongitude(6.569420238493345);
//        location.setAltitude(0);
//        location.setTime(System.currentTimeMillis());
//        LocalDatabase.getInstance().setLocation(location);
        PhotoObject po = TestPhotoObjectUtils.paulVanDykPO();
        pictureID = po.getPictureId();
        po.upload();

        LocalDatabase.getInstance().addPhotoObject(po);
        displayFullsizeImageIntent = new Intent();
        displayFullsizeImageIntent.putExtra(ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID, pictureID);

    }

    @Test
    public void launchFullPictureActivity() throws Exception{
        mActivityTestRule.launchActivity(displayFullsizeImageIntent);
        Thread.sleep(1000);
        onView(withText("Up !!")).perform(click());
        Thread.sleep(1000);
        onView(withText("Down")).perform(click());

    }

    @After
    public void clearPO(){
        DatabaseRef.deletePhotoObjectFromDB(pictureID);
        StorageRef.deletePictureFromStorage(pictureID);
    }
}