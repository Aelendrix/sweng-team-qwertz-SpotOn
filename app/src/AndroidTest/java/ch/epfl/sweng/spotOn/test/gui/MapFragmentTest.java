package ch.epfl.sweng.spotOn.test.gui;

import android.app.Instrumentation;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.media.PhotoObject;
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

    UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    @Before
    public void getSomePinDisplayed(){
        PhotoObject po1 = TestPhotoObjectUtils.iceDivingPO();
        PhotoObject po2 = TestPhotoObjectUtils.germaynDeryckePO();
    }
    @Test
    public void clickingOnMarkerTest() {
        //Go to the map fragment
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withId(R.id.viewpager)).perform(swipeLeft());

        UiObject pin = mDevice.findObject(new UiSelector().descriptionContains(""));
    }
}
