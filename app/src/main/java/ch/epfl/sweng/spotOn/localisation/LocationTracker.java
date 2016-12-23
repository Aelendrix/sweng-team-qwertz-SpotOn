package ch.epfl.sweng.spotOn.localisation;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by quentin on 16.11.16.
 *
 */

public interface LocationTracker {

    int TIMEOUT_LOCATION = 60 * 1000;  // 1 minutes

    int LISTENERS_NOTIFICATION_NEW_LOCATION = 0;
    int LISTENERS_NOTIFICATION_LOCATION_TIMEOUT = 1;



    boolean hasValidLocation();

    Location getLocation();

    LatLng getLatLng();

    void addListener(LocationTrackerListener l);

}
