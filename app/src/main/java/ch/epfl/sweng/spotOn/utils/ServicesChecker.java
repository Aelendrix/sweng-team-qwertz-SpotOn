package ch.epfl.sweng.spotOn.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

/**
 * Created by quentin on 17.11.16.
 */

public class ServicesChecker {

    private static ServicesChecker mSingleInstance=null;

    private boolean databaseConnectionStatus;


// INITIALIZE AND CONSTRUCTOR
    public static void initialize(){
        mSingleInstance = new ServicesChecker();

    }
    private ServicesChecker(){
        if( !ConcreteLocationTracker.instanceExists() || ! !LocalDatabase.instanceExists() ){
            throw new IllegalStateException("Must initialize LocationTracker and Localdatabase first");
        }
        databaseConnectionStatus = false;
    }

// PUBLIC METHODS
    public boolean instanceExists(){
        return mSingleInstance!=null;
    }

    public ServicesChecker getInstance(){
        if(!instanceExists()){
            throw new IllegalStateException("Hasn't been initialized yet");
        }
        return mSingleInstance;
    }

    public  boolean statusIsOk(){
        if(     ! ConcreteLocationTracker.instanceExists() ||
                ! LocalDatabase.instanceExists() ||
                ! ConcreteLocationTracker.getInstance().hasValidLocation() ||
                ! databaseConnectionStatus)
        {
            return false;
        }else{
            return true;
        }
    }

    public String provideErrorMessage(){

    }

    private void listenForDatabaseConnectivity(){
        DatabaseRef.getRootDirectory().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = dataSnapshot.getValue(Boolean.class);
                if(connected){

                }else{

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


// LISTENER PATTERN METHODS
    public void notifyListeners()

}
