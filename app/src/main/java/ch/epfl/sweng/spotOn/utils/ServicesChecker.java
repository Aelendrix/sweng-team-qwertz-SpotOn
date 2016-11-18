
package ch.epfl.sweng.spotOn.utils;

import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabaseListener;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTrackerListener;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

/**
 * Created by quentin on 17.11.16.
 */

public class ServicesChecker implements LocationTrackerListener, LocalDatabaseListener{

    private static ServicesChecker mSingleInstance=null;

    private LocationTracker mLocationTrackerRef;
    private LocalDatabase mLocalDatabaseRef;

    private boolean databaseConnectionStatus;
    private boolean validLocationStatus;

    private List<ServicesCheckerListener> mListeners;


// INITIALIZE AND CONSTRUCTOR
    public static void initialize(LocationTracker ltref, LocalDatabase ldbref){
        mSingleInstance = new ServicesChecker(ltref, ldbref);
        mSingleInstance.listenForDatabaseConnectivity();
        ltref.addListener(mSingleInstance);
        ldbref.addListener(mSingleInstance);
    }
    private ServicesChecker(LocationTracker ltref, LocalDatabase ldbref){
        if( !ConcreteLocationTracker.instanceExists() || !LocalDatabase.instanceExists() ){
            // test just in case
            throw new IllegalStateException("Must initialize LocationTracker and Localdatabase first");
        }
        mListeners = new ArrayList<>();
        mLocalDatabaseRef = ldbref;
        mLocationTrackerRef = ltref;
        databaseConnectionStatus = false;
        validLocationStatus = ltref.hasValidLocation();
    }

// PUBLIC METHODS
    public static  boolean instanceExists(){
        return mSingleInstance!=null;
    }

    public static ServicesChecker getInstance() {
        if (!instanceExists()) {
            throw new IllegalStateException("ServicesChecker hasn't been initialized yet");
        }
        return mSingleInstance;
    }

    public void addListener(ServicesCheckerListener l ){
        mListeners.add(l);
        l.servicesAvailabilityUpdated();
    }

    public  boolean statusIsOk(){
        if( ! ConcreteLocationTracker.getInstance().hasValidLocation() || ! databaseConnectionStatus){
            return false;
        }else{
            return true;
        }
    }

    public String provideErrorMessage(){
        String errorMessage= "";
        if( !mLocationTrackerRef.hasValidLocation() ){
            errorMessage += "no valid Location ";
        }
        if( !databaseConnectionStatus ){
            errorMessage += "no database connection";
        }
        return errorMessage;
    }


// PRIVATE METHODS

    /** adds a listener to keep track of the connection to database  */
    private void listenForDatabaseConnectivity(){
        DatabaseRef.getRootDirectory().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                Log.d("ServicesChecker", " database connection statue : "+connected);
                if(connected){
                    databaseConnectionStatus = true;
                }else{
                    databaseConnectionStatus = false;
                }
                notifyListeners();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseConnectionStatus = false;
                notifyListeners();
                Log.d("ServicesChecker", "cancelled database query");
            }
        });
    }


// LISTENER PATTERN METHODS
    public void notifyListeners(){
        Log.d("ServicesChecker","updating listeners");
        for( ServicesCheckerListener l : mListeners){
            l.servicesAvailabilityUpdated();
        }
    }

// LISTENER METHODS -- todo
    @Override
    public void databaseUpdated() {
        // nothing to do, we have another listener for that
    }

    @Override
    public void updateLocation(Location newLocation) {
        if( ! validLocationStatus){
            // change in services status
            validLocationStatus = true;
            notifyListeners();
        } // else, no change in status
    }

    @Override
    public void locationTimedOut() {
        if(validLocationStatus){
            // change in services status
            validLocationStatus = false;
            notifyListeners();
        } // else, no change in status
    }
}
