package ch.epfl.sweng.spotOn.FirebaseConnectionTracker;

/**
 * Created by quentin on 07.12.16.
 * Interface of a listener of the firebase connection
 */
public interface FirebaseConnectionListener {

    /**
     * method called when the firebase connection is open
     */
    void firebaseDatabaseConnected();
    /**
     * method called when the firebase connection is closed
     */
    void firebaseDatabaseDisconnected();
}
