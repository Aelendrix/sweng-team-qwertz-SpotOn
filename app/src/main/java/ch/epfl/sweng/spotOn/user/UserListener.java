package ch.epfl.sweng.spotOn.user;

/**
 * Created by quentin on 24.11.16.
 * Interface of a userListener
 */
public interface UserListener {
    /**
     * called once when the user is connected
     */
    void userConnected();

    /**
     * called once when the user is disconnected
     */
    void userDisconnected();
}
