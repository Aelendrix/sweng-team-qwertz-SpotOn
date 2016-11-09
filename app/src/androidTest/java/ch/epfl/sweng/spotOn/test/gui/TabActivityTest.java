package ch.epfl.sweng.spotOn.test.gui;


import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.AboutPage;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.gui.UserProfileActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.LocalisationTracker;


/**
 * Created by Alexis Dewaele on 26/10/2016.
 * This class tests the TabActivity
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TabActivityTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class);

    @Test
    public void swipe_between_fragments() {
        Intents.init();
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withText("Camera")).perform(click());
        onView(withId(R.id.viewpager)).perform(swipeRight());
        Intents.release();
    }

    @Test
    public void aboutPagePopsUp() {
        Intents.init();
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText("About")).perform(click());
        intended(hasComponent(AboutPage.class.getName()));
        Intents.release();
    }


    @Test
    public void startUserProfileActivity() {
        Intents.init();
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText("Profile")).perform(click());
        intended(hasComponent(UserProfileActivity.class.getName()));
        onView(withText("Back")).perform(click());
        intended(hasComponent(TabActivity.class.getName()));
        Intents.release();
    }



    @Test
    public void refreshDB() throws Exception{
        Intents.init();
        Location location = new Location("testLocationProvider");
        location.setLatitude(46.52890355757567);
        location.setLongitude(6.569420238493345);
        location.setAltitude(0);
        location.setTime(System.currentTimeMillis());
        LocalDatabase.setLocation(location);

        TabActivity tabActivity = mActivityTestRule.getActivity();

        tabActivity.mLocationTracker.refreshTrackerLocation();
        Thread.sleep(10000);
        Intents.release();

    }
}

