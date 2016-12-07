package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.MainActivity;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.user.UserManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by quentin on 29.11.16.
 */

@RunWith(AndroidJUnit4.class)
public class mainActivityTest_notLoggedInButton {

    UiDevice mDevice;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class, false, false);

    @Before
    public void initSuff(){
        TestInitUtils.initContextNoUser();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        if(UserManager.instanceExists()) {
            UserManager.getInstance().destroyUser();
        }
    }

    @Test
    public void mainActivityNotLoggedInTest() throws InterruptedException {
        mActivityTestRule.launchActivity(new Intent());
        onView(withId(R.id.dontLogInButton)).perform(click());

        // wait for transition to tabActivity
        mDevice.waitForWindowUpdate(null,10000);
        Thread.sleep(2000);
        onView(withId(R.id.log_out)).perform(click());
    }



}
