package ch.epfl.sweng.spotOn.test.util;

import ch.epfl.sweng.spotOn.FirebaseConnectionTracker.FirebaseConnectionListener;
import ch.epfl.sweng.spotOn.FirebaseConnectionTracker.FirebaseConnectionTracker;

/** a mock FirebaseConnectionTRacker that always report as connected, and never notifies its listeners
 */
public class MockFirebaseConnectionTracker_forTests implements FirebaseConnectionTracker {


    @Override
    public void addListener(FirebaseConnectionListener l) {
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
