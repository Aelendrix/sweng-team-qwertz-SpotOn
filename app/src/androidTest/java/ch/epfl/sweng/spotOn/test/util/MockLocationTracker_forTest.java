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

public class MockLocationTracker_forTest implements LocationTracker {

    List<LocationTrackerListener> listeners = new ArrayList<>();
    Location mockLocation = null;

    public MockLocationTracker_forTest(Location loc){
        mockLocation=loc;
    }
    public MockLocationTracker_forTest(double latitude, double longitude){
        Location newMockLocation = new Location("MLT_FT_mockProvider");
        newMockLocation.setProvider("MLT_FT_mockProvider");
        newMockLocation.setLatitude(latitude);
        newMockLocation.setLongitude(longitude);
        newMockLocation.setAltitude(10);
        newMockLocation.setAccuracy(new Float(0.90));
        newMockLocation.setTime(new Date().getTime());
        mockLocation=newMockLocation;
    }
    public MockLocationTracker_forTest(){
        Location newMockLocation = new Location("MLT_FT_mockProvider");
        newMockLocation.setProvider("MLT_FT_mockProvider");
        newMockLocation.setLatitude(46.52942111671832);
        newMockLocation.setLongitude(6.569539974900668);
        newMockLocation.setAltitude(10);
        newMockLocation.setAccuracy(new Float(0.90));
        newMockLocation.setTime(new Date().getTime());
        mockLocation=newMockLocation;
    }

    @Override
    public boolean hasValidLocation() {
        return mockLocation!=null;
    }

    @Override
    public Location getLocation() {
        if(mockLocation==null){
            throw new IllegalStateException();
        }
        return mockLocation;
    }

    @Override
    public LatLng getLatLng() {
        if(mockLocation==null){
            throw new IllegalStateException();
        }
        return new LatLng(getLocation().getLatitude(), getLocation().getLongitude());
    }

    @Override
    public void addListener(LocationTrackerListener l) {
        listeners.add(l);
        if(mockLocation!=null){
            l.updateLocation(mockLocation);
        }
    }

    public void forceLocationChange(Location newLoc){
        mockLocation=newLoc;
        notifyListners(LISTENERS_NOTIFICATION_NEW_LOCATION);
    }

    public void forceLocationTimeout(){
        notifyListners(LISTENERS_NOTIFICATION_LOCATION_TIMEOUT);
        mockLocation = null;
    }

    public void setMockLocation(Location l){
        mockLocation=l;
    }


// PRIVATE METHODS

    private void notifyListners(int notification){
        if(notification != LISTENERS_NOTIFICATION_LOCATION_TIMEOUT && notification != LISTENERS_NOTIFICATION_NEW_LOCATION){
            throw new IllegalArgumentException("Location tracker - notifyListner() - wrong notify message : "+notification);
        }else if (notification == LISTENERS_NOTIFICATION_LOCATION_TIMEOUT){
            for(LocationTrackerListener listener : listeners){
                listener.locationTimedOut(mockLocation);
            }
        }else{
            for(LocationTrackerListener listener : listeners){
                listener.updateLocation(mockLocation);
            }
        }
    }
}
