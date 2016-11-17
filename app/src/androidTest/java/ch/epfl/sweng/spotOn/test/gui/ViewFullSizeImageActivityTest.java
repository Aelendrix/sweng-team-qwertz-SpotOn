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

import org.junit.After;
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
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
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
    private List<String> picsIds;
    public String pictureID1;
    public String pictureID2;
    public Intent displayFullSizeImageIntent;
    @Before
    public void getPictureID(){
        picsIds = initLocalDatabase();
        pictureID1 = picsIds.get(0);
        pictureID2 = picsIds.get(1);
        displayFullSizeImageIntent = new Intent();
        displayFullSizeImageIntent.putExtra(ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID, pictureID1);

    }

    @Test
    public void launchFullPictureActivity() throws InterruptedException{
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
        Thread.sleep(1000);
        onView(withId(R.id.upvoteButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.downvoteButton)).perform(click());
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
        LocalDatabase.deletePhotoObject(pictureID1);
        LocalDatabase.deletePhotoObject(pictureID2);
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
        po1.setRadiusMax();
        String pictureID3 = po1.getPictureId();
        picIDs.add(pictureID3);
        po1.upload(true, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
        PhotoObject po2 = TestPhotoObjectUtils.germaynDeryckePO();
        String pictureID4 = po2.getPictureId();
        picIDs.add(pictureID4);
        po2.upload(true, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });

        LocalDatabase.addPhotoObject(po1);
        LocalDatabase.addPhotoObject(po2);
        return picIDs;
    }
}
