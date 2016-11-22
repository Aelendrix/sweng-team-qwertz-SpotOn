package ch.epfl.sweng.spotOn.test.gui;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.MapFragment;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.user.User;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by nico on 16.11.16.
 */
@RunWith(AndroidJUnit4.class)
public class MapTest {

    MockLocationTracker_forTest mMockLocationTracker;

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class){
        @Override
        public void beforeActivityLaunched(){
            mMockLocationTracker = new MockLocationTracker_forTest();
            LocalDatabase.initialize(mMockLocationTracker);
            ConcreteLocationTracker.setMockLocationTracker(mMockLocationTracker);
            User.initializeFromFb("Sweng", "Sweng", "114110565725225");
        }
    };

    @Test
    public void refreshLocalisationMarker() throws Exception{
        onView(withId(R.id.viewpager)).perform(swipeRight());
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        Thread.sleep(5000);

        final MapFragment mapFragment = (MapFragment) mActivityTestRule.getActivity().getSupportFragmentManager().getFragments().get(2);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mMockLocationTracker.forceLocationChange(createLocation0());
                ConcreteLocationTracker.setMockLocationTracker(mMockLocationTracker);
                mapFragment.refreshMapLocation();
                mMockLocationTracker.forceLocationChange(createLocation1());
                ConcreteLocationTracker.setMockLocationTracker(mMockLocationTracker);
                mapFragment.refreshMapLocation();
            }

        });
    }



// PRIVATE HELPERS

    private Location createLocation0(){
        Location l0 = new Location("mockProvider");
        l0.setLatitude(0);
        l0.setLongitude(0);
        l0.setTime(new Date().getTime());
        return l0;
    }
    private Location createLocation1(){
        Location l1 = new Location("mockProvider");
        l1.setLatitude(1);
        l1.setLongitude(1);
        l1.setTime(new Date().getTime());
        return l1;
    }
}

