package ch.epfl.sweng.spotOn.test.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTrackerListener;

/** A mock location tracker that never has a valid location
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
