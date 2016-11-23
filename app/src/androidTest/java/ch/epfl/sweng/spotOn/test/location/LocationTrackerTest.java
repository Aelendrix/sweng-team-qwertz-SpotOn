package ch.epfl.sweng.spotOn.test.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocalizationUtils;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;

/**
 * Created by nico on 16.11.16.
 */

@RunWith(AndroidJUnit4.class)
public class LocationTrackerTest extends AndroidTestCase{

    private Location location0 = new Location(LocationManager.GPS_PROVIDER);
    private Location location1 = new Location(LocationManager.GPS_PROVIDER);
    private Location location2 = new Location("testLocationProvider");
    private Location location3 = new Location("testLocationProvider");


//    @Rule
    public IntentsTestRule<TabActivity> intentsRule = new IntentsTestRule<TabActivity>(TabActivity.class);


// INITIALIZATION

    @Before
    public void init() {

        location3.setLatitude(0);
        location3.setLongitude(0);
        location3.setAltitude(0);
        location3.setAccuracy(100);
        location3.setTime(100);

        location2.setLatitude(0);
        location2.setLongitude(0);
        location2.setAltitude(0);
        location2.setAccuracy(1);
        location2.setTime(1);

        location1.setLatitude(1);
        location1.setLongitude(1);
        location1.setAltitude(0);
        location1.setAccuracy(2);
        location1.setProvider(LocationManager.GPS_PROVIDER);
        location1.setTime(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            location1.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }else{
            throw new IllegalStateException("Tests need api 17 to work");
        }

        location0.setLatitude(1);
        location0.setLongitude(1);
        location0.setAltitude(0);
        location0.setAccuracy(1);
        location0.setTime(System.currentTimeMillis());
        location0.setProvider(LocationManager.GPS_PROVIDER);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            location0.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }else{
            throw new IllegalStateException("Tests need api 17 to work");
        }

        ConcreteLocationTracker.initialize(new MockLocationManagerWrapper());
        LocalDatabase.initialize(ConcreteLocationTracker.getInstance());
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance());

    }



    @Test
    public void testMockProvider() throws InterruptedException {

        intentsRule.launchActivity(new Intent());

        Thread.sleep(5000);

        ConcreteLocationTracker.getInstance().getLocation();


    }



    @Test
    public void testIsBestLocation(){
        if (!LocalizationUtils.isBetterLocation(location2, null)) {
            throw new AssertionError("a location is better that no location at all");
        }
        if (!LocalizationUtils.isBetterLocation(location1, location2)) {
            throw new AssertionError("a very new (+2min) location is better than an old one");
        }
        if (LocalizationUtils.isBetterLocation(location2, location1)) {
            throw new AssertionError("the location compared should be too old to be useful");
        }
        if (!LocalizationUtils.isBetterLocation(location0, location1)) {
            throw new AssertionError("the new location should be more accurate than the old one");
        }
        if (LocalizationUtils.isBetterLocation(location1, location0)) {
            throw new AssertionError("the new location is less accurate");
        }
        if (!LocalizationUtils.isBetterLocation(location3, location2)) {
            throw new AssertionError("the new location is less accurate but is newer from the same locationprovider");
        }
    }

    @Test
    public void testIsSameProvider(){
        if (!LocalizationUtils.isSameProvider("provider1", "provider1")) {
            throw new AssertionError("should be same provider");
        }
        if (LocalizationUtils.isSameProvider(null, "provider1")) {
            throw new AssertionError("should be same provider");
        }
    }
}
