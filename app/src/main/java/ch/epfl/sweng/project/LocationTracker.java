package ch.epfl.sweng.project;


import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import static android.content.Context.LOCATION_SERVICE;

public class LocationTracker implements LocationListener{

    private final int TIME_BETWEEN_LOCALISATION =  60000; //1 minutes
    private final int MIN_DISTANCE_CHANGE_UPDATE = 1; // 1 Meter

    boolean isLocationEnable=false;
    private Location location;

    public static double latitude;
    public static double longitude;
    LocationManager locationManager;


    public LocationTracker(LocationManager locationManager)
    {
        this.locationManager = locationManager;
    }

    //return the Location of the listener (can return null)
    public Location getLocation() throws SecurityException{

        // getting GPS status
        isLocationEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
