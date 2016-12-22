
package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.MainActivity;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.user.UserManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * Test of the main activity
 * Created by nico on 22.11.16.
 */

// NOTE: This test only work on a phone without Facebook app installed on it and in the current facebook login interface
@RunWith(AndroidJUnit4.class)
public class LogInOutTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class, false, false){
        @Override
        public void beforeActivityLaunched(){
            Location location = new Location("testLocationProvider");
            location.setLatitude(0.52890355757567);
            location.setLongitude(0.569420238493345);
            location.setAltitude(0);
            location.setTime(System.currentTimeMillis());

            TestInitUtils.initContextNoUser(location);
        }
    };

    UiDevice mDevice;

    @Before
    public void setUp() throws Exception{
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        if(UserManager.instanceExists()) {
            UserManager.getInstance().destroyUser();
        }
    }

    @Test
    //this test can fail randomly cause it depends of retrieving a webview from facebook
    public void logInAndOut() throws Exception {
        mActivityTestRule.launchActivity(new Intent());
        onView(withId(R.id.mainLoginButton)).perform(click());
        //Thread.sleep(4000); // sorry... my phone is slow

        UiObject input = mDevice.findObject(new UiSelector().instance(0).className(EditText.class));
        input.setText("swengqwertz@gmail.com");
        UiObject input2 = mDevice.findObject(new UiSelector().instance(1).className(EditText.class));
        input2.setText("123swengisfun321");
        UiObject buttonInput = mDevice.findObject(new UiSelector().instance(0).className(Button.class));
        buttonInput.click();
        mDevice.waitForWindowUpdate(null,6000);
        //mDevice.wait(Until.hasObject(By.textContains("OK")),6000);
        buttonInput = mDevice.findObject(new UiSelector().instance(1).className(Button.class));
        buttonInput.click();
        //wait to come back to mainActivity
        mDevice.waitForWindowUpdate(null,10000);
        //wait the mainActivity to start TabActivity
        mDevice.waitForWindowUpdate(null,10000);
        Thread.sleep(2000);
        onView(withId(R.id.log_out)).perform(click());
        onView(withText("Cancel")).perform(click());
        onView(withId(R.id.log_out)).perform(click());
        onView(withText("Log Out")).perform(click());


    }
}

