package ch.epfl.sweng.spotOn.test.location;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocalizationUtils;
import ch.epfl.sweng.spotOn.localisation.LocationTrackerListener;
import ch.epfl.sweng.spotOn.test.util.MockLocationManagerWrapper_forTests;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;

@RunWith(AndroidJUnit4.class)
public class LocationTrackerTest{

    private Location location0;
    private Location location1;
    private Location location2;
    private Location location3;

    private MockLocationManagerWrapper_forTests mlm;

    private Object lock = new Object();



// INITIALIZATION
    @Before
    public void initializeForTest(){
        initFieldLocations();

        // DON'T USE TESTINITUTILS since we need to keep the mlm field + we're not using a mockLocationTracker, but a ConcreteLocationTracker with a mock LocationManager
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        mlm = new MockLocationManagerWrapper_forTests(location0);
        ConcreteLocationTracker.initialize(mlm);

        LocalDatabase.initialize(ConcreteLocationTracker.getInstance());
        UserManager.initialize();
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance());
        UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");
    }



// TESTS
    @Test
    public void testMockProvider() throws InterruptedException {

        if( ! ConcreteLocationTracker.instanceExists() ){
            throw new AssertionError("LocationTracker instance should exist");
        }
        if( ! ConcreteLocationTracker.getInstance().hasValidLocation() ){
            throw new AssertionError("LocationTracker instance should have valid Location");
        }

        Location obtainedLocation = ConcreteLocationTracker.getInstance().getLocation();
        if( ! locationsAtSamePlace(obtainedLocation, location0) ){
            throw new AssertionError("\n"+obtainedLocation+"\n     ---- should be equals to ----\n"+location0);
        }

    }

    @Test
    public void testLatLong(){
        if( !ConcreteLocationTracker.instanceExists() || !ConcreteLocationTracker.getInstance().hasValidLocation()){
            throw new AssertionError("LocationTrackerTest : testLatLong : No ConcreteLocationTracker instance ("+!ConcreteLocationTracker.instanceExists()+") or no valid location ("+!ConcreteLocationTracker.getInstance().hasValidLocation()+")");
        }

        Location currentLoc = ConcreteLocationTracker.getInstance().getLocation();
        LatLng ll = ConcreteLocationTracker.getInstance().getLatLng();

        if(currentLoc.getLatitude()!=ll.latitude || currentLoc.getLongitude()!=ll.longitude){
            throw new AssertionError("locationTracker returned different positional values");
        }
    }

    @Test (expected = IllegalStateException.class)
    public void timeOutTest() throws InterruptedException {
        if( !ConcreteLocationTracker.instanceExists() || !ConcreteLocationTracker.getInstance().hasValidLocation()){
            throw new AssertionError("LocationTrackerTest : testLatLong");
        }
        ConcreteLocationTracker.getInstance().addListener(new LocationTrackerListener() {
            @Override
            public void updateLocation(Location newLocation) {
                // nothing
            }
            @Override
            public void locationTimedOut(Location old) {
                synchronized (lock){
                    lock.notify();
                }
            }
        });

        mlm.triggerTimeout();

        synchronized (lock){
            lock.wait();
        }

        Thread.sleep(500);
        if(ConcreteLocationTracker.getInstance().hasValidLocation()){
            throw new AssertionError("Should be timedout");
        }
        ConcreteLocationTracker.getInstance().getLocation(); // should throw a IllegalStateException, indicating that test is complete
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





// HELPERS

    // compares latitude, longitude, altitude, accuracy, time, elapsedTime
    private static boolean locationsAtSamePlace(Location l1, Location l2){
        if(l1.getProvider() != l2.getProvider()){
            return false;
        }  else  if(l1.getAccuracy() != l2.getAccuracy()){
            return false;
        } else if(l1.getAltitude() != l2.getAltitude()){
            return false;
        } else if(l1.getLongitude() != l2.getLongitude()){
            return false;
        } else if(l1.getLatitude() != l2.getLatitude()){
            return false;
        } else {
            return true;
        }
    }

    private void initFieldLocations(){
        location3 = new Location("LTT_mockProvider");
        location2 = new Location("LTT_mockProvider");
        location1 = new Location("LTT_mockProvider");
        location0 = new Location("LTT_mockProvider");

        location3.setLatitude(0);
        location3.setLongitude(0);
        location3.setAltitude(0);
        location3.setAccuracy(100);
        location3.setTime(100);
        location3.setProvider(LocationManager.GPS_PROVIDER);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            location3.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }else{
            throw new IllegalStateException("Tests need api 17 to work");
        }
        location2.setLatitude(0);
        location2.setLongitude(0);
        location2.setAltitude(0);
        location2.setAccuracy(1);
        location2.setTime(1);
        location2.setProvider(LocationManager.GPS_PROVIDER);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            location2.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }else{
            throw new IllegalStateException("Tests need api 17 to work");
        }

        location1.setLatitude(1);
        location1.setLongitude(1);
        location1.setAltitude(0);
        location1.setAccuracy(2);
        location1.setProvider(LocationManager.GPS_PROVIDER);
        location1.setTime(System.currentTimeMillis());
        location1.setProvider(LocationManager.GPS_PROVIDER);
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
        location0.setProvider(LocationManager.GPS_PROVIDER);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            location0.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }else{
            throw new IllegalStateException("Tests need api 17 to work");
        }
    }

}
