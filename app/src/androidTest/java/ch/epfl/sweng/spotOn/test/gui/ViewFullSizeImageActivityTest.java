package ch.epfl.sweng.spotOn.test.gui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.NonNull;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.gui.ViewFullsizeImageActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.TestPhotoObjectUtils;

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
    public String pictureID;
    public Intent displayFullsizeImageIntent;
    @Before
    public void getPictureID(){
        pictureID = initLocalDatabase().get(0);
        displayFullsizeImageIntent = new Intent();
        displayFullsizeImageIntent.putExtra(ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID, pictureID);

    }
    @Test
    public void launchFullPictureActivity() throws InterruptedException{
        mActivityTestRule.launchActivity(displayFullsizeImageIntent);
        Thread.sleep(1000);
        onView(withText("Up !!")).perform(click());
        Thread.sleep(1000);
        onView(withText("Down")).perform(click());
    }

    @Test
    public void swipeBetweenPicturesTest() throws InterruptedException{
        mActivityTestRule.launchActivity(displayFullsizeImageIntent);
        Thread.sleep(1000);
        onView(withId(R.id.pager)).perform(swipeLeft());
    }

    /**
     * Initialize the local database with 2 sample pictures (useful for testing)
     * @return the list of picture IDs pictures added in the local database
     */
    public static List<String> initLocalDatabase() {
        List<String> picIDs = new ArrayList<>();
        Location location = new Location("testLocationProvider");
        location.setLatitude(46.52890355757567);
        location.setLongitude(6.569420238493345);
        location.setAltitude(0);
        location.setTime(System.currentTimeMillis());
        LocalDatabase.clearData();
        LocalDatabase.setLocation(location);
        PhotoObject po1 = TestPhotoObjectUtils.paulVanDykPO();
        String pictureID1 = po1.getPictureId();
        picIDs.add(pictureID1);
        /*po1.upload(true, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });*/
        PhotoObject po2 = TestPhotoObjectUtils.germaynDeryckePO();
        String pictureID2 = po2.getPictureId();
        picIDs.add(pictureID2);
        /*po2.upload(true, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });*/
        LocalDatabase.addPhotoObject(po1);
        LocalDatabase.addPhotoObject(po2);
        return picIDs;
    }
}
