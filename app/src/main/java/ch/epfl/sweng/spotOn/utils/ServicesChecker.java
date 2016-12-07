
package ch.epfl.sweng.spotOn.utils;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTrackerListener;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.user.UserListener;
import ch.epfl.sweng.spotOn.user.UserManager;

/**
 * Created by quentin on 17.11.16.
 */

public class ServicesChecker implements LocationTrackerListener, UserListener {

    private static ServicesChecker mSingleInstance=null;

    private LocationTracker mLocationTrackerRef;
    private UserManager mUserManagerRef;

    private boolean databaseIsConnected;

    // need to keep track of the previous state of a service to detect change in the service availability ( available -> available should not trigger anything, while unavailable -> available should)
    private boolean locationIsValid;
    private boolean userIsLoggedIn;




// INITIALIZE AND CONSTRUCTOR
    public static void initialize(LocationTracker ltref, LocalDatabase ldbref, UserManager userRef){
        mSingleInstance = new ServicesChecker(ltref, ldbref, userRef);
        mSingleInstance.listenForDatabaseConnectivity();
        mSingleInstance.mLocationTrackerRef.addListener(mSingleInstance);
        mSingleInstance.mUserManagerRef.addListener(mSingleInstance);
    }

    private ServicesChecker(LocationTracker ltref, LocalDatabase ldbref, UserManager userRef){
        if( ltref==null || ldbref==null|| userRef==null){
            // test to enforce that all required singletons are instanciated
            throw new IllegalStateException("Must initialize LocationTracker, Localdatabase and UserManager first");
        }
        // we keep the LocalDatabase reference in the method prototype, to enforce that ServicesChecker relies on an existing instance of LocalDatabase
        mLocationTrackerRef = ltref;
        mUserManagerRef = userRef;
        locationIsValid = ltref.hasValidLocation();
        userIsLoggedIn = mUserManagerRef.userIsLoggedIn();
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

    public boolean allServicesOk(){
        // duplicates allowedToPost for new, but I'd like to keep it that way (1) for the abstraction and (2) because it might change later and I'd like to keep the same name
        return databaseIsConnected && mLocationTrackerRef.hasValidLocation() && mUserManagerRef.userIsLoggedIn();
    }

    public boolean databaseConnected(){
        return databaseIsConnected;
    }

    public String provideErrorMessage(){
        String errorMessage = "";
        if( ! databaseIsConnected ){
            errorMessage += "Can't connect to the database\n";
        }
        if( ! mLocationTrackerRef.hasValidLocation() ){
            errorMessage += "Can't localize your device\n";
        }
        if( ! mUserManagerRef.userIsLoggedIn() ){
            if( ! mUserManagerRef.getUser().getIsRetrievedFromDB()){
                errorMessage+= "We're processing your login informations\n";
            }else {
                errorMessage += "You're not logged in\n";
            }
        }
        if(!allServicesOk()) {
            errorMessage += "--  Some features will be restricted  --";
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
                Log.d("ServicesChecker", " database connection status : "+connected);
                databaseConnectionUpdated(connected);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseConnectionUpdated(false);
                Log.d("ServicesChecker", "ERROR : cancelled database query");
            }
        });
    }




// LISTENER METHODS

    public void databaseConnectionUpdated(boolean isNowConnected){
        if(isNowConnected){
            if(!databaseIsConnected){ // disconnected -> connected
                databaseIsConnected = true;
                if(allServicesOk()){
                    ToastProvider.printOverCurrent("All services are now OK", Toast.LENGTH_SHORT);
                }else{
                    ToastProvider.printOverCurrent(provideErrorMessage(), Toast.LENGTH_LONG);
                }
            }
        }else{
            if(databaseIsConnected){ // connected -> connected
                databaseIsConnected = false;
                ToastProvider.printOverCurrent(provideErrorMessage(), Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void updateLocation(Location newLocation) {
        if( ! locationIsValid){         // check for bad -> good transition
            Log.d("ServicesChecker","location status changed : listeners notified");
            locationIsValid = true;
            printOkMessage();
        }
    }

    @Override
    public void locationTimedOut(Location old) {
        if(locationIsValid){            // check for good -> bad transition
            Log.d("ServicesChecker","location timedout : listeners notified");
            locationIsValid = false;
            ToastProvider.printOverCurrent(provideErrorMessage(), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void userConnected() {
        if( !userIsLoggedIn ){          // check for bad -> good transition
            Log.d("ServicesChecker","user logged in : listeners notified");
            userIsLoggedIn=true;
            printOkMessage();
        }
    }

    @Override
    public void userDisconnected() {
        if( userIsLoggedIn ){           // check for good -> bad transition
            Log.d("ServicesChecker","user logged out : listeners notified");
            userIsLoggedIn=false;
            ToastProvider.printOverCurrent(provideErrorMessage(), Toast.LENGTH_LONG);
        }
    }

// PRIVATE HELPERS
    private void printOkMessage(){
        if(allServicesOk()){
            ToastProvider.printOverCurrent("All services are now OK", Toast.LENGTH_SHORT);
        }
    }
}
