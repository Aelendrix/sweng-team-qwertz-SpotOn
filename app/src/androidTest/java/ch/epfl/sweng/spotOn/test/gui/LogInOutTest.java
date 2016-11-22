package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by nico on 22.11.16.
 */
//TODO: NOTE: This test only work on a phone whitout Facebook app on it and in the current facebook login interface
public class LogInOutTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);
    UiDevice mDevice;

    @Before
    public void setUp() throws Exception{
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void launchFullPictureActivity() throws Exception{
        mActivityTestRule.launchActivity(new Intent());
        onView(withId(R.id.mainLoginButton)).perform(click());
        Thread.sleep(1000);
        UiObject input = mDevice.findObject(new UiSelector().instance(0).className(EditText.class));
        input.setText("swengqwertz@gmail.com");
        Thread.sleep(2000);

        UiObject input2 = mDevice.findObject(new UiSelector().instance(1).className(EditText.class));
        input2.setText("123swengisfun321");
        Thread.sleep(2000);

        UiObject buttonInput = mDevice.findObject(new UiSelector().instance(0).className(Button.class));
        buttonInput.click();
        Thread.sleep(5000);

    }
}
