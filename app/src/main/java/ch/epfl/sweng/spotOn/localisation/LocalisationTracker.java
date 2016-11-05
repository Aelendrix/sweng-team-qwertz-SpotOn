package ch.epfl.sweng.spotOn.localisation;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;

public class LocalisationTracker {

    LocationManager mLocationManager;
    Location mLocation;

    public LocalisationTracker(Context c) {

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener gpsLocationListener = new LocationListener() {
            public void onLocationChanged(Location newLocation) {
                if(isBetterLocation(newLocation ,mLocation)){
                    mLocation = newLocation;
                    Log.d("LocationTracker","new location refreshed from " +mLocation.getProvider() +": lat "+mLocation.getLatitude()+" long "+mLocation.getLongitude());
                }
                LocalDatabase.setLocation(mLocation);
                refreshTrackerLocation();

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        final int TIME_BETWEEN_LOCALISATION = 2 * 1000; //2 Second
        final int MIN_DISTANCE_CHANGE_UPDATE = 0; // 0 Meter
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_BETWEEN_LOCALISATION, MIN_DISTANCE_CHANGE_UPDATE, gpsLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_BETWEEN_LOCALISATION, MIN_DISTANCE_CHANGE_UPDATE, gpsLocationListener);

        }

        /*Catch exception because location acces always need to have the localisation permission
        * In our app if the permission is rejected, we can't access this activity anyway (ATM)
        */ catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private final int THRESHOLD_ONE_MINUTE = 1000*60; //1 minute


    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        // Filter 2 locations in function of time
        long timeDiff = location.getTime() - currentBestLocation.getTime();
        boolean isNewer = timeDiff > 0;
        if (timeDiff > THRESHOLD_ONE_MINUTE) {
            return true;
        } else if (-timeDiff > THRESHOLD_ONE_MINUTE) {
            return false;
        }

        // Filter 2 location in function of accuracy
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());

        // Determine location quality using a combination of timeliness and accuracy
        if (accuracyDelta < 0) {
            return true;
        } else if (isNewer &&
                accuracyDelta < 200 &&
                isSameProvider(location.getProvider(), currentBestLocation.getProvider())) {
            return true;
        }
        return false;
    }

    //Compare 2 providers
    private boolean isSameProvider(String provider1, String provider2) {
        return provider2 != null && provider2.equals(provider1);
    }

    //Overrided public method used in tabActivity
    public void refreshTrackerLocation(){}

}
