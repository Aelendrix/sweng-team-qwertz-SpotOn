package ch.epfl.sweng.spotOn.localisation;


import android.location.LocationListener;
import android.location.LocationManager;

/**
 *  Wrapper for the class LocationManager, needed for testing the LocationTracker with mock locations
 */

public class ConcreteLocationManagerWrapper implements LocationManagerWrapper {

    private LocationManager mUnderlyingLocationManager;

    public ConcreteLocationManagerWrapper(LocationManager lm) {
        mUnderlyingLocationManager = lm;
    }

    @Override
    public void requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener) throws SecurityException {
        mUnderlyingLocationManager.requestLocationUpdates(provider, minTime, minDistance, listener);
    }

    @Override
    public void removeUpdates(LocationListener l) throws SecurityException {
        mUnderlyingLocationManager.removeUpdates(l);
    }
}
