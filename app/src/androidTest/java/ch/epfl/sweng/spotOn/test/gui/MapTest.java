package ch.epfl.sweng.spotOn.test.gui;

import android.location.Location;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import ch.epfl.sweng.spotOn.FirebaseConnectionTracker.ConcreteFirebaseConnectionTracker;
import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.MapFragment;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Unit test of the map methods, currently no global test of the map
 * Created by nico on 16.11.16.
 */
@RunWith(AndroidJUnit4.class)
public class MapTest {

    MockLocationTracker_forTest mMockLocationTracker;

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class){
        @Override
        public void beforeActivityLaunched(){
            // DON'T USE TESTUTILS to initialize - here, we need a reference to mlm
            if(ConcreteLocationTracker.instanceExists()){
                ConcreteLocationTracker.destroyInstance();
            }

            mMockLocationTracker = new MockLocationTracker_forTest();
            ConcreteLocationTracker.setMockLocationTracker(mMockLocationTracker);

            LocalDatabase.initialize(mMockLocationTracker);
            UserManager.initialize();
            ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
            UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");
        }
    };

    @Test
    public void refreshLocalisationMarker() throws Exception{
        onView(withId(R.id.viewpager)).perform(swipeRight());
        Thread.sleep(1000);
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        Thread.sleep(1000);
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        Thread.sleep(2000);

        final MapFragment mapFragment = (MapFragment) mActivityTestRule.getActivity().getSupportFragmentManager().getFragments().get(2);

        mMockLocationTracker.forceLocationChange(createLocation0());
        ConcreteLocationTracker.setMockLocationTracker(mMockLocationTracker);
        mapFragment.refreshMapLocation();
        mMockLocationTracker.forceLocationChange(createLocation1());
        ConcreteLocationTracker.setMockLocationTracker(mMockLocationTracker);
        mapFragment.refreshMapLocation();
    }

    @After
    public void after(){
        ConcreteLocationTracker.destroyInstance();
        if( ConcreteLocationTracker.instanceExists()){
            throw new AssertionError("MapTest : concreteLocationTracker mock instance not deleted");
        }
    }



// PRIVATE HELPERS

    private Location createLocation0(){
        Location l0 = new Location("MapTest_mockProvider");
        l0.setLatitude(0);
        l0.setLongitude(0);
        l0.setTime(new Date().getTime());
        return l0;
    }
    private Location createLocation1(){
        Location l1 = new Location("MapTest_mockProvider");
        l1.setLatitude(1);
        l1.setLongitude(1);
        l1.setTime(new Date().getTime());
        return l1;
    }
}

