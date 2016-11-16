package ch.epfl.sweng.spotOn.test.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTrackerListener;

/**
 * Created by quentin on 16.11.16.
 */

public class MockLocationTracker implements LocationTracker {

    List<LocationTrackerListener> listeners = new ArrayList<>();
    Location mockLocation = null;

    public MockLocationTracker(double latitude, double longitude){
        Location newMockLocation = new Location("mockProvider");
        newMockLocation.setLatitude(latitude);
        newMockLocation.setLongitude(longitude);
        newMockLocation.setAltitude(10);
        newMockLocation.setAccuracy(new Float(0.90));
        newMockLocation.setTime(new Date().getTime());
        mockLocation=newMockLocation;
    }
    public MockLocationTracker(){
        Location newMockLocation = new Location("mockProvider");
        newMockLocation.setLatitude(46.52942111671832);
        newMockLocation.setLongitude(6.569539974900668);
        newMockLocation.setAltitude(10);
        newMockLocation.setAccuracy(new Float(0.90));
        mockLocation=newMockLocation;
    }

    @Override
    public boolean hasValidLocation() {
        return true;
    }

    @Override
    public Location getLocation() {
        return mockLocation;
    }

    @Override
    public LatLng getLatLng() {
        return new LatLng(getLocation().getLatitude(), getLocation().getLongitude());
    }

    @Override
    public void addListener(LocationTrackerListener l) {
        listeners.add(l);
    }
}
