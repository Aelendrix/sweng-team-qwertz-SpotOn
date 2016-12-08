package ch.epfl.sweng.spotOn.localisation;

import android.location.Location;

/** Provides methods allowing the location tracker to notify an object of location changes
 */

public interface LocationTrackerListener {

    void updateLocation(Location newLocation);

    void locationTimedOut(Location timedOutLocation);
}
