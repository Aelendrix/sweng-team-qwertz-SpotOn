
package ch.epfl.sweng.spotOn.utils;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.user.UserManager;

/**
 * Created by quentin on 17.11.16.
 */

//public class ServicesChecker implements LocationTrackerListener, LocalDatabaseListener, UserListener{
public class ServicesChecker {

    private static ServicesChecker mSingleInstance=null;

    private LocationTracker mLocationTrackerRef;
    private UserManager mUserManagerRef;

    private boolean databaseIsConnected;

//    private boolean locationIsValid;
//    private boolean userIsLoggedIn;

//    private List<ServicesCheckerListener> mListeners;




// INITIALIZE AND CONSTRUCTOR
    public static void initialize(LocationTracker ltref, LocalDatabase ldbref, UserManager userRef){
        mSingleInstance = new ServicesChecker(ltref, ldbref, userRef);
        mSingleInstance.listenForDatabaseConnectivity();
//        mSingleInstance.mLocationTrackerRef.addListener(mSingleInstance);
//        mSingleInstance.mLocalDatabaseRef.addListener(mSingleInstance);
    }

    private ServicesChecker(LocationTracker ltref, LocalDatabase ldbref, UserManager userRef){
        if( ltref==null || ldbref==null|| userRef==null){
            // test to enforce that all required singletons are instantiated
            throw new IllegalStateException("Must initialize LocationTracker, LocalDatabase and UserManager first");
        }
        // we keep the LocalDatabase reference in the method prototype, to enforce that ServicesChecker relies on an existing instance of LocalDatabase
//        mListeners = new ArrayList<>();
        mLocationTrackerRef = ltref;
//        databaseConnectionStatus = false;
//        validLocationStatus = ltref.hasValidLocation();
        mUserManagerRef = userRef;
        //userIsLoggedIn = mUserRef.userIsLoggedIn();
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

//    public void addListener(ServicesCheckerListener l ){
//        mListeners.add(l);
//        l.servicesAvailabilityUpdated();
//    }

    public boolean allServicesOk(){
        // duplicates allowedToPost for new, but I'd like to keep it that way (1) for the abstraction and (2) because it might change later and I'd like to keep the same name
        return databaseIsConnected && mLocationTrackerRef.hasValidLocation() && mUserManagerRef.userIsLoggedIn();
    }

    public boolean allowedToPost(){
        return databaseIsConnected && mLocationTrackerRef.hasValidLocation() && mUserManagerRef.userIsLoggedIn();
    }

    public boolean allowedToViewPosts(){
        return databaseIsConnected && mLocationTrackerRef.hasValidLocation();
    }

    public String provideErrorMessage(){
        String errorMessage = "";
        if( ! databaseIsConnected ){
            errorMessage += "Can't connect to the database\n";
        }
        if( ! mLocationTrackerRef.hasValidLocation() ){
            errorMessage += "Can't localize your device\n";
        }
        if( ! mUserManagerRef.getInstance().userIsLoggedIn() ){
            errorMessage += "You're not logged in\n";
        }
        if(!allServicesOk()) {
            errorMessage += "--  App may malfunction  --";
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
                if(connected){
                    databaseIsConnected = true;
                }else{
                    databaseIsConnected = false;
                }
//                notifyListeners();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseIsConnected = false;
//                notifyListeners();
                Log.d("ServicesChecker", "ERROR : cancelled database query");
            }
        });
    }




// LISTENER PATTERN METHODS
//    public void notifyListeners(){
//        for( ServicesCheckerListener l : mListeners){
//            l.servicesAvailabilityUpdated();
//        }
//    }




// LISTENER METHODS -- todo
//    @Override
//    public void databaseUpdated() {
//        // nothing to do, we have another listener for that
//    }
//
//    @Override
//    public void updateLocation(Location newLocation) {
//        if( ! locationIsValid){            // change in services status
//            Log.d("ServicesChecker","location status changed : listeners notified");
//            locationIsValid = true;
////            notifyListeners();
//        }
//    }
//
//    @Override
//    public void locationTimedOut(Location old) {
//        if(locationIsValid){           // change in services status
//            Log.d("ServicesChecker","location timed out : listeners notified");
//            locationIsValid = false;
////            notifyListeners();
//        }
//    }

//    @Override
//    public void userConnected() {
//        if( !userIsLoggedIn ){
//            Log.d("ServicesChecker","user logged in : listeners notified");
//            userIsLoggedIn=true;
//            notifyListeners();
//        }
//    }
//
//    @Override
//    public void userDisconnected() {
//        if( userIsLoggedIn ){
//            Log.d("ServicesChecker","user logged out : listeners notified");
//            userIsLoggedIn=false;
//            notifyListeners();
//        }
//    }
}
