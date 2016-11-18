package ch.epfl.sweng.spotOn.localisation;

import android.location.Location;

/**
 * Created by quentin on 17.11.16.
 */

public class LocalizationUtils {

    // decides if newLocation is better than currentBestLocation in term of recentness, and accuracy
    static public boolean isBetterLocation(Location newlocation, Location currentBestLocation) {
        final int THRESHOLD_ONE_MINUTE = 60*1000;
        if (currentBestLocation == null) {
            return true;
        }

        // Filter 2 locations in function of time
        long timeDiff = newlocation.getTime() - currentBestLocation.getTime();
        boolean isNewer = timeDiff > 0;
        if (timeDiff > THRESHOLD_ONE_MINUTE) {
            return true;
        } else if (-timeDiff > THRESHOLD_ONE_MINUTE) {
            return false;
        }

        // Filter 2 location in function of accuracy
        int accuracyDelta = (int) (newlocation.getAccuracy() - currentBestLocation.getAccuracy());

        // Determine location quality using a combination of timeliness and accuracy
        if (accuracyDelta < 0) {
            return true;
        } else if (isNewer &&
                accuracyDelta < 200 &&
                LocalizationUtils.isSameProvider(newlocation.getProvider(), currentBestLocation.getProvider())) {
            return true;
        }
        return false;
    }

    // Compare 2 providers
    static public boolean isSameProvider(String provider1, String provider2) {
        return provider2 != null && provider2.equals(provider1);
    }
}
