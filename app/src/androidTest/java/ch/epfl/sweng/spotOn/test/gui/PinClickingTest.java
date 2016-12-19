package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.LocalDatabaseTestUtils;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by olivi on 18.12.2016.
 */

public class PinClickingTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class,true,false);

    @Before
    public void initLocalDatabase() throws InterruptedException {
        LocalDatabaseTestUtils.initLocalDatabase(false);
    }

    @Test
    public void clickOnPinTest() throws Exception{
        mActivityTestRule.launchActivity(new Intent());
        onView(withText(R.string.tab_map)).check(matches(isDisplayed()));
        onView(withText(R.string.tab_map)).perform(click());
        Thread.sleep(2000);
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        //Picture ID of the first element in the localDatabase
        String markerTitle = LocalDatabase.getInstance().getViewableMedias().keySet().iterator().next();
        UiObject marker = device.findObject(new UiSelector().descriptionContains(markerTitle));
        marker.click();
        Thread.sleep(2000);
    }

    @After
    public void after() {
        LocalDatabaseTestUtils.afterTests();
    }
}
