package ch.epfl.sweng.spotOn.test.gui;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.AboutPage;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.gui.UserProfileActivity;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.util.MockUser_forTests;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;

/**
 * Created by Alexis Dewaele on 26/10/2016.
 * This class tests the TabActivity
 */

@RunWith(AndroidJUnit4.class)
public class TabActivityTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class)
    {
        @Override
        public void beforeActivityLaunched(){
            User mockUser = new MockUser_forTests("julius","caius","caesar", 1000, new HashMap<String, Long>(), true, true);
            TestInitUtils.initContextMockUser(mockUser);
        }
    };

//    @Before
//    public void setUpContext(){
//        User mockUser = new MockUser_forTests("julius","caius","caesar", 1000, new HashMap<String, Long>(), true, true);
//        TestInitUtils.initContextMockUser(mockUser);
//    }

    @Test
    public void swipe_between_fragments() {
        onView(withText(R.string.tab_aroundme)).check(matches(isDisplayed()));
        onView(withText(R.string.tab_camera)).check(matches(isDisplayed()));
        onView(withText(R.string.tab_map)).check(matches(isDisplayed()));
        onView(withText(R.string.tab_aroundme)).perform(click());
        onView(withText(R.string.tab_camera)).perform(click());
        onView(withText(R.string.tab_map)).perform(click());
    }

    @Test
    public void press_back_button() {
        //proc a toast
        Intents.init();
        final TabActivity tabActivity = mActivityTestRule.getActivity();
        tabActivity.runOnUiThread(new Runnable() {
            public void run() {
               tabActivity.onBackPressed();
            }
        });
        Intents.release();
    }

    @Test
    public void aboutPagePopsUp() {
        Intents.init();
        onView(withId(R.id.action_about)).perform(click());
        intended(hasComponent(AboutPage.class.getName()));
        Intents.release();
    }

    @Test
    public void startUserProfileActivity() {
        Intents.init();
        if(!UserManager.getInstance().userIsLoggedIn() || !UserManager.getInstance().getUser().getIsRetrievedFromDB() || !UserManager.getInstance().isLogInThroughFacebook()){
            throw new AssertionError();
        }


        onView(withId(R.id.user_profile)).perform(click());
        intended(hasComponent(UserProfileActivity.class.getName()));
        Espresso.pressBack();
        Intents.release();
    }

    @After
    public void after(){
        ConcreteLocationTracker.destroyInstance();
        if( ConcreteLocationTracker.instanceExists()){
            throw new AssertionError("TabActivityTest : concreteLocationTracker mock instance not deleted");
        }
        UserManager.getInstance().destroyUser();
    }
}