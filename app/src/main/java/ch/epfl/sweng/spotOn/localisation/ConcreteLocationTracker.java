package ch.epfl.sweng.spotOn.localisation;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/** Singleton object providing location data to the app. Needs to be explicitly initialized
 */
public final class ConcreteLocationTracker implements LocationTracker {

    private static LocationTracker mSingleInstance=null;

    private LocationManagerWrapper mLocationManager;
    private LocationListener mCurrentLocationListener;
    private Handler mLocationTimeoutHandler;
    private Runnable mRunOnTimeout;

    private List<LocationTrackerListener> mListenersList;
    private Location mLocation;


// INITIALIZE AND CONSTRUCTOR AND DESTROY
    public static void initialize(LocationManagerWrapper locManager){
        if(mSingleInstance==null){
            mSingleInstance = new ConcreteLocationTracker(locManager);
        }
    }

    private ConcreteLocationTracker(LocationManagerWrapper locManager) {
        // for listeners
        mListenersList = new ArrayList<>();
        // runnable that will take care of timeout-ing the location after a given time
        mLocationTimeoutHandler = new Handler(Looper.getMainLooper());
        mRunOnTimeout  = new Runnable() {
            @Override
            public void run() {
                Log.d("LocationTracker", "timeout finished");
                // synchronized, to prevent race conditions with another thread setting mLocation to a new location while we're notifying the listeners
                synchronized (ConcreteLocationTracker.getInstance()){
                    notifyListeners(LISTENERS_NOTIFICATION_LOCATION_TIMEOUT);
                    mLocation=null;
                }
                // no need to restart here, once timeout, we wait for a new location to start countdown again
            }
        };
        // Acquire a reference to the system Location Manager
        mLocationManager = locManager;
        // Define a listener that responds to location updates
        mCurrentLocationListener = new LocationListener() {
            public void onLocationChanged(Location newLocation) {
                if(LocalizationUtils.isBetterLocation(newLocation ,mLocation)){
                    Log.d("LocationTracker","location updated by provider : "+newLocation.getProvider()+" at time "+Calendar.getInstance().getTimeInMillis());
                    mLocation = newLocation;
                    notifyListeners(LISTENERS_NOTIFICATION_NEW_LOCATION);
                    mLocationTimeoutHandler.removeCallbacks(mRunOnTimeout);
                    mLocationTimeoutHandler.postDelayed(mRunOnTimeout, TIMEOUT_LOCATION);
                }
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {         }
            public void onProviderEnabled(String provider) {            }
            public void onProviderDisabled(String provider) {            }
        };

        // Register the listener with the Location Manager to receive location updates
        final int TIME_BETWEEN_LOCALISATION = 2 * 1000; //2 Second
        final int MIN_DISTANCE_CHANGE_UPDATE = 0; // 0 Meter
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_BETWEEN_LOCALISATION, MIN_DISTANCE_CHANGE_UPDATE, mCurrentLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_BETWEEN_LOCALISATION, MIN_DISTANCE_CHANGE_UPDATE, mCurrentLocationListener);

        }

            /*Catch exception because location access always need to have the localisation permission
            * In our app if the permission is rejected, we can't access this activity anyway (ATM)
            */
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    /** very needed for tests, since apparently, the singleton is not initialized once per test file, but once for ALL test files
     *  And we want to have a mockLocationTracker sometimes, and sometimes test the concreteLocationTracker to which we input mock arguments
     */
    public static void destroyInstance() {
        mSingleInstance = null;
    }



// PUBLIC METHODS
    public static boolean instanceExists(){
        return mSingleInstance!=null;
    }

    public static LocationTracker getInstance(){
        if(mSingleInstance==null){
            throw new IllegalStateException("ConcreteLocationTracker has not been initialized");
        }
        return mSingleInstance;
    }

    public boolean hasValidLocation(){
        if(mLocation==null){
            return false;
        }else {
            return mLocation.getTime() - Calendar.getInstance().getTime().getTime() <= TIMEOUT_LOCATION;
        }
    }

    public Location getLocation(){
        if(hasValidLocation()){
            return mLocation;
        }else{
            throw new IllegalStateException("The LocationTracker holds no valid position at the moment - check locationIsValid before calling getLocation");
        }
    }

    public LatLng getLatLng(){
        if(hasValidLocation()) {
            return new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        }
        else {
            throw new IllegalStateException("The LocationTracker holds no valid position at the moment - check locationIsValid before calling getLocation");
        }
    }

    public void addListener(LocationTrackerListener l){
        mListenersList.add(l);
        if(hasValidLocation()){
            l.updateLocation(mLocation);
        }else{
            l.locationTimedOut(null);
        }
    }

    //for testing only !
    public static void setMockLocationTracker(LocationTracker mlt){
        if(mSingleInstance==null){
            mSingleInstance=mlt;
        }
    }

// FOR LISTENERS
    private void notifyListeners(int notification){
        if(notification != LISTENERS_NOTIFICATION_LOCATION_TIMEOUT && notification != LISTENERS_NOTIFICATION_NEW_LOCATION){
            throw new IllegalArgumentException("Location tracker - notifyListener() - wrong notify message : "+notification);
        }else if (notification == LISTENERS_NOTIFICATION_LOCATION_TIMEOUT){
            for(LocationTrackerListener listener : mListenersList){
                listener.locationTimedOut(mLocation);
            }
        }else{
            for(LocationTrackerListener listener : mListenersList){
                listener.updateLocation(mLocation);
            }
        }
    }

}
