package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by quentin on 29.11.16.
 */

@RunWith(AndroidJUnit4.class)
public class mainActivityTest_notLoggedInButton {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void initStuff(){
        TestInitUtils.initContextNoUser();
        if(UserManager.instanceExists()) {
            UserManager.getInstance().destroyUser();
        }
    }

    @Test
    public void mainActivityNotLoggedInTest() throws InterruptedException {
        mActivityTestRule.launchActivity(new Intent());
        //in the MainActivity
        Thread.sleep(2000);
        onView(withId(R.id.dontLogInButton)).check(matches(isDisplayed()));
        onView(withId(R.id.dontLogInButton)).perform(click());

        //in the TabActivity
        onView(withId(R.id.log_out)).check(matches(isDisplayed()));
        onView(withId(R.id.log_out)).perform(click());
        onView(withText("Cancel")).perform(click());
        onView(withId(R.id.log_out)).perform(click());
        onView(withText("Log Out")).perform(click());

        //in the MainActivity
        onView(withId(R.id.dontLogInButton)).check(matches(isDisplayed()));
    }




}
