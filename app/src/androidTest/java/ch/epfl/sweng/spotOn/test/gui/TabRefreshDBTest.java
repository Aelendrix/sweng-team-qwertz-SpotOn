package ch.epfl.sweng.spotOn.test.gui;

import android.location.Location;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;

/**
 * Created by nico on 09.11.16.
 */
@RunWith(AndroidJUnit4.class)
public class TabRefreshDBTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class);

    @Test
    public void refreshDB() throws Exception{
        Intents.init();
        Location location = new Location("testLocationProvider");
        location.setLatitude(46.52890355757567);
        location.setLongitude(6.569420238493345);
        location.setAltitude(0);
        location.setTime(System.currentTimeMillis());
        LocalDatabase.setLocation(location);

        TabActivity tabActivity = (TabActivity) mActivityTestRule.getActivity();

        tabActivity.mLocationTracker.refreshTrackerLocation();
        Thread.sleep(10000);
        Intents.release();

    }
}
