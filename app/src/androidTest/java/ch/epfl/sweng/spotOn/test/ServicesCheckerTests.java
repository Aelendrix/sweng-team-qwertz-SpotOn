package ch.epfl.sweng.spotOn.test;

import android.app.Activity;
import android.location.Location;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.test.util.MockUser_forTests;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;

/**
 * Created by quentin on 29.11.16.
 */

public class ServicesCheckerTests {

    ServicesChecker sc;
    User mMockUser;
    MockLocationTracker_forTest mlt;

    @Before
    public void initializeServicesChecker(){

        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        mlt = new MockLocationTracker_forTest();
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        UserManager.initialize();
        mMockUser = new MockUser_forTests("some","name","UUID",0l,new HashMap<String, Long>(), true);
        UserManager.getInstance().setMockUser(mMockUser);

        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance());
    }

    @Test
    public void testCorrectErrorMessage() throws InterruptedException {

        String expected = "";

        // no connection to database
        expected = "Can't connect to the database\n"+"--  App may malfunction  --";
        checkExpected(expected);


        Thread.sleep(7000); // needed for Database to initialize correctly

        // no correct user
        UserManager.getInstance().destroyUser();
        UserManager.getInstance().setEmptyUser();
        expected = "You're not logged in\n"+"--  App may malfunction  --";
        checkExpected(expected);

        // correct state
        UserManager.getInstance().destroyUser();
        UserManager.getInstance().setMockUser(mMockUser);
        expected = "";
        checkExpected(expected);

        // no correct Location
        mlt.forceLocationTimeout();
        expected = "Can't localize your device\n"+"--  App may malfunction  --";
        checkExpected(expected);


    }

    private void checkExpected(String expected){
        String errMsg = ServicesChecker.getInstance().provideErrorMessage();
        if( ! errMsg.equals(expected)){
            throw new AssertionError("Error messages was :\n"+errMsg+"\n\nWhile expecting :\n"+expected+"\n\n");
        }
    }

    // tester : avec un user pas loggé      -   sans location   -   sans database
    // -> EmptyUser / mockUser
    // -> mock Location / good location
    //

//    String errorMessage = "";
//    if( ! mLocationTrackerRef.hasValidLocation() ){
//        errorMessage += "Can't localize your device\n";
//    }
//    if( ! databaseIsConnected ){
//        errorMessage += "Can't connect to the database\n";
//    }
//    if( ! mUserManagerRef.getInstance().userIsLoggedIn() ){
//        errorMessage += "You're not logged in\n";
//    }
//    errorMessage += "--  App may malfunction  --";
//    return errorMessage;

}
