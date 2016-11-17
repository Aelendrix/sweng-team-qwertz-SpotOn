package ch.epfl.sweng.spotOn.test.gui;

import android.app.Instrumentation;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.test.util.TestPhotoObjectUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
/**
 * Created by olivi on 09.11.2016.
 */

@RunWith(AndroidJUnit4.class)
public class MapFragmentTest {

    @Rule
    public IntentsTestRule<TabActivity> intentsRule = new IntentsTestRule<>(TabActivity.class);
    //Get the pictureID associated to the first picture of the Local DB (also the title of the marker)
    public List<String> pictureIDs = ViewFullSizeImageActivityTest.initLocalDatabase();
    public String pictureID1 = pictureIDs.get(0);
    public String pictureID2 = pictureIDs.get(1);

    public void goToMapFragment() throws Exception {
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        Thread.sleep(1000);
    }

    @Test
    public void displayClusterTest() throws Exception {
        PhotoObject picToAdd = TestPhotoObjectUtils.iceDivingPO();
        LocalDatabase.addPhotoObject(picToAdd);
        goToMapFragment();
        Thread.sleep(1000);
    }

    @Test
    public void clickingOnMarkerTest() throws Exception {
        goToMapFragment();
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject pin = mDevice.findObject(new UiSelector().descriptionContains(pictureID1));
        pin.click();
        Thread.sleep(1000);
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

}
