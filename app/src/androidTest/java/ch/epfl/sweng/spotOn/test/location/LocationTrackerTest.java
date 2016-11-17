package ch.epfl.sweng.spotOn.test.location;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.localisation.LocalizationUtils;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;

/**
 * Created by nico on 16.11.16.
 */

@RunWith(AndroidJUnit4.class)
public class LocationTrackerTest {

    private LocationTracker lt;

    private Location location0 = new Location("testLocationProvider");
    private Location location1 = new Location("testLocationProvider");
    private Location location2 = new Location("testLocationProvider");
    private Location location3 = new Location("testLocationProvider");


    @Before
    public void initLocation() {

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
        location1.setTime(2*60*1000);

        location0.setLatitude(1);
        location0.setLongitude(1);
        location0.setAltitude(0);
        location0.setAccuracy(1);
        location0.setTime(2*60*1000);

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
// old way
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                lt = new LocalisationTracker(InstrumentationRegistry.getContext());
//
//                if (!lt.isSameProvider("provider1", "provider1")) {
//                    throw new AssertionError("should be same provider");
//                }
//
//                if (lt.isSameProvider(null, "provider1")) {
//                    throw new AssertionError("should be same provider");
//                }
//
//            }
//        });
    }
}
