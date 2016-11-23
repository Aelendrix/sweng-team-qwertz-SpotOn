package ch.epfl.sweng.spotOn.localisation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

/** Wrapper for the class LocationManager, needed for testing the LocationTracker with mock locations
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
}
