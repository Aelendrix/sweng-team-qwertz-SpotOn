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

    public void forceLocationChange(Location newLoc){
        mockLocation=newLoc;
        notifyListners(LISTENERS_NOTIFICATION_NEW_LOCATION);
    }

    public void forceLocationTimeout(){
        notifyListners(LISTENERS_NOTIFICATION_LOCATION_TIMEOUT);
    }


// PRIVATE METHODS

    private void notifyListners(int notification){
        if(notification != LISTENERS_NOTIFICATION_LOCATION_TIMEOUT && notification != LISTENERS_NOTIFICATION_NEW_LOCATION){
            throw new IllegalArgumentException("Location tracker - notifyListner() - wrong notify message : "+notification);
        }else if (notification == LISTENERS_NOTIFICATION_LOCATION_TIMEOUT){
            for(LocationTrackerListener listener : listeners){
                listener.locationTimedOut();
            }
        }else{
            for(LocationTrackerListener listener : listeners){
                listener.updateLocation(mockLocation);
            }
        }
    }
}
