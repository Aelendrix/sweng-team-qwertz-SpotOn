package ch.epfl.sweng.spotOn.test.util;

import ch.epfl.sweng.spotOn.FirebaseConnectionTracker.FirebaseConnectionListener;
import ch.epfl.sweng.spotOn.FirebaseConnectionTracker.FirebaseConnectionTracker;

/**
 * Created by quentin on 07.12.16.
 */

public class MockFirebaseConnectionTracker_forTests implements FirebaseConnectionTracker {


    @Override
    public void addListener(FirebaseConnectionListener l) {
        // nothing
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
