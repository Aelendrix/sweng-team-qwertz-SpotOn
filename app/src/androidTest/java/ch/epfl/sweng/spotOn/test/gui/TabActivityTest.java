package ch.epfl.sweng.spotOn.test.gui;


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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.AboutPage;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.gui.UserProfileActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.location.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.user.User;


/**
 * Created by Alexis Dewaele on 26/10/2016.
 * This class tests the TabActivity
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TabActivityTest {


    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class){
        @Override
        public void beforeActivityLaunched(){

            // destroy LocationTrackerSingleton if need be
            if(ConcreteLocationTracker.instanceExists()){
                ConcreteLocationTracker.getInstance().destroyInstance();
            }

            MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
            ConcreteLocationTracker.setMockLocationTracker(mlt);

            LocalDatabase.initialize(mlt);
            User.initializeFromFb("Sweng", "Sweng", "114110565725225");
        }
    };

    @Test
    public void swipe_between_fragments() {
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withText("Camera")).perform(click());
        onView(withId(R.id.viewpager)).perform(swipeRight());
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
        onView(withId(R.id.profileBackButton)).perform(click());
        Intents.release();
    }


    @Test
    public void clickRotate() throws InterruptedException {
        onView(withText("Camera")).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.rotateButton)).perform(click());
    }
}