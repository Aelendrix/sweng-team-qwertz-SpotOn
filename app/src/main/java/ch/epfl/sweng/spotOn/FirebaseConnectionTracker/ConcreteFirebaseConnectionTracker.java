package ch.epfl.sweng.spotOn.FirebaseConnectionTracker;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

/**
 * Created by quentin on 07.12.16.
 * This monitor whether or not we have a connection with firebase and provide listener interface for connection/disconnection event
 */

public class ConcreteFirebaseConnectionTracker implements FirebaseConnectionTracker {

    private List<FirebaseConnectionListener> listeners;
    private static ConcreteFirebaseConnectionTracker mSingleInstance;

    private static boolean currentStatus;

    private ConcreteFirebaseConnectionTracker(){
        currentStatus = false;
        listeners = new ArrayList<>();
        DatabaseRef.getRootDirectory().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                currentStatus = connected;
                Log.d("FirebaseConnTracker", " database connection status : "+connected);
                notifyListeners();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                currentStatus=false;
                notifyListeners();
                Log.d("FirebaseConnTracker", "ERROR : cancelled database query");
            }
        });
    }

    /**
     * Get the reference of the only object, since we use the Singleton pattern
     * @return mSingleInstance, the instance of the singleton
     */
    public static ConcreteFirebaseConnectionTracker getInstance() {
        if (mSingleInstance == null){
            mSingleInstance = new ConcreteFirebaseConnectionTracker();
        }
        return mSingleInstance;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(FirebaseConnectionListener l){
        listeners.add(l);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConnected(){
        return currentStatus;
    }


// PRIVATE METHODS
    private void notifyListeners(){
        if(currentStatus){
            for(FirebaseConnectionListener l : listeners){
                l.firebaseDatabaseConnected();
            }
        }else{
            for(FirebaseConnectionListener l : listeners){
                l.firebaseDatabaseDisconnected();
            }
        }
    }
}
