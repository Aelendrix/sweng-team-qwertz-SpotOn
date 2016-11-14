package ch.epfl.sweng.spotOn.test.gui;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

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
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.spotOn.test.gui.TestImageViewCatcher.hasDrawable;
import static org.hamcrest.Matchers.not;

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
        Location location = new Location("testLocationProvider");
        location.setLatitude(46.52890355757567);
        location.setLongitude(6.569420238493345);
        location.setAltitude(0);
        location.setTime(System.currentTimeMillis());
        LocalDatabase.setLocation(location);
        PhotoObject po = TestPhotoObjectUtils.paulVanDykPO();
        pictureID = po.getPictureId();
        po.upload(true, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {}
        });

        LocalDatabase.addPhotoObject(po);
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