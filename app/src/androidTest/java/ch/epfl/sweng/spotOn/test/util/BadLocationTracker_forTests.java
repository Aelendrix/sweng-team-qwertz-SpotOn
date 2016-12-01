package ch.epfl.sweng.spotOn.test.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTrackerListener;

/**
 * Created by quentin on 29.11.16.
 */

public class BadLocationTracker_forTests implements LocationTracker {
    @Override
    public boolean hasValidLocation() {
        return false;
    }

    @Override
    public Location getLocation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LatLng getLatLng() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addListener(LocationTrackerListener l) {
        // nothing
    }
}
