package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.MainActivity;
import ch.epfl.sweng.spotOn.gui.UserProfileActivity;
import ch.epfl.sweng.spotOn.user.User;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by nico on 22.11.16.
 */
//TODO: NOTE: This test only work on a phone whitout Facebook app on it and in the current facebook login interface
@RunWith(AndroidJUnit4.class)
public class LogInOutTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);
    UiDevice mDevice;

    @Before
    public void setUp() throws Exception{
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        User.getInstance().destroy();
    }

    @Test
    public void logInAndOut() throws Exception{
        mActivityTestRule.launchActivity(new Intent());
        onView(withId(R.id.mainLoginButton)).perform(click());
        Thread.sleep(1000);
        UiObject input = mDevice.findObject(new UiSelector().instance(0).className(EditText.class));
        input.setText("swengqwertz@gmail.com");
        Thread.sleep(100);

        UiObject input2 = mDevice.findObject(new UiSelector().instance(1).className(EditText.class));
        input2.setText("123swengisfun321");
        Thread.sleep(100);

        UiObject buttonInput = mDevice.findObject(new UiSelector().instance(0).className(Button.class));
        buttonInput.click();
        Thread.sleep(8000);
        buttonInput = mDevice.findObject(new UiSelector().instance(1).className(Button.class));
        buttonInput.click();
        Thread.sleep(10000);
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText("Log out")).perform(click());
        Thread.sleep(1000);

    }
}
