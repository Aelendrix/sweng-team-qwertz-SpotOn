package ch.epfl.sweng.spotOn.localisation;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by quentin on 16.11.16.
 */

public interface LocationTracker {

    boolean hasValidLocation();

    Location getLocation();

    LatLng getLatLng();

    void addListener(LocationTrackerListener l);

}
