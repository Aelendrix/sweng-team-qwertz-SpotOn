package ch.epfl.sweng.spotOn.FirebaseConnectionTracker;

/**
 * Created by quentin on 07.12.16.
 */

public interface FirebaseConnectionListener {

    void firebaseDatabaseConnected();
    void firebaseDatabaseDisconnected();
}
