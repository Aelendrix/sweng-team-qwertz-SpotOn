package ch.epfl.sweng.spotOn.test.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.spotOn.localisation.LocationManagerWrapper;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;

/**
 * Created by quentin on 23.11.16.
 */

public class MockLocationManager implements LocationManagerWrapper {

    private Location mLocation;

    private List<LocationListener> mListeners = new ArrayList<LocationListener>();

    private Handler fakeNewLocationHandler;
    private Runnable runForNewLocation;



// CONSTRUCTORS

    public MockLocationManager(Location l){
        mLocation = l;
        fakeNewLocationHandler = new Handler();
        runForNewLocation = new Runnable() {
            @Override
            public void run() {
                if( ! mListeners.isEmpty() ){
                    for(LocationListener l : mListeners){
                        l.onLocationChanged(mLocation);
                    }
                }
                fakeNewLocationHandler.postDelayed(this, 1000);
            }
        };
        fakeNewLocationHandler.post(runForNewLocation);

    }

    public MockLocationManager(){
        mLocation = cookSomeLocation();
        fakeNewLocationHandler = new Handler();
        runForNewLocation = new Runnable() {
            @Override
            public void run() {
                if( ! mListeners.isEmpty() ){
                    for(LocationListener l : mListeners){
                        l.onLocationChanged(mLocation);
                    }
                }
                fakeNewLocationHandler.postDelayed(this, 1000);
            }
        };
        fakeNewLocationHandler.post(runForNewLocation);
    }



// INTERFACE METHODS

    @Override
    public void requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener) {
        mListeners.add(listener);
        listener.onLocationChanged(mLocation);
    }



// HELPER METHODS
    public Location cookSomeLocation(){
        Location location0 = new Location("MockProvider");
        location0.setLatitude(1);
        location0.setLongitude(1);
        location0.setAltitude(0);
        location0.setAccuracy(1);
        location0.setTime(System.currentTimeMillis());
        location0.setProvider(LocationManager.GPS_PROVIDER);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            location0.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }else{
            throw new IllegalStateException("Tests need api 17 to work");
        }
        return location0;
    }
}
