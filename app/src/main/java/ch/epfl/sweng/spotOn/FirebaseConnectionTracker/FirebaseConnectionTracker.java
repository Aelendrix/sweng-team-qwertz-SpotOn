package ch.epfl.sweng.spotOn.FirebaseConnectionTracker;

/**
 * Created by quentin on 07.12.16.
 * Interface of : this monitor whether or not we have a connection with firebase and provide listener interface for connection/disconnection event
 */

public interface FirebaseConnectionTracker {
    /**
     * Add a listener to the list of listener
     * @param l a listener of the firebase server
     */
    void addListener(FirebaseConnectionListener l);

    /**
     * check if the firebase server is connected
     * @return true if the server is connected, false otherwise
     */
    boolean isConnected();
}
