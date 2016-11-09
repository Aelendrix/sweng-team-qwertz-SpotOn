package ch.epfl.sweng.spotOn.test.gui;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.MainActivity;
import ch.epfl.sweng.spotOn.gui.ViewFullsizeImageActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    //TODO: change this test cause it'll fail if the mainActivity jump to tabActivity
    //and couldn't click the mainActivity Button
    @Test
    public void launchFullPictureActivity() throws Exception{
        Thread.sleep(2000);
        onView(withId(R.id.mainLoginButton)).perform(click());
        Thread.sleep(2000);

    }

}
