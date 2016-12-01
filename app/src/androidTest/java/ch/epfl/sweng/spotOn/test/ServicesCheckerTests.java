package ch.epfl.sweng.spotOn.test;

import android.app.Activity;
import android.location.Location;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
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
        mMockUser = new MockUser_forTests("some","name","UUID",0l,new HashMap<String, Long>(), true, true);
        UserManager.getInstance().setMockUser(mMockUser);

        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance());
    }

    @Test
    public void testCorrectErrorMessage() throws InterruptedException {

        String expected = "";

        // testing connection to database is too random, need to think about it

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
        Location tempStoreLocation = mlt.getLocation();
        mlt.forceLocationTimeout();
        expected = "Can't localize your device\n"+"--  App may malfunction  --";
        checkExpected(expected);

        // restore location
        mlt.setMockLocation(tempStoreLocation);
    }

    private void checkExpected(String expected){
        String errMsg = ServicesChecker.getInstance().provideErrorMessage();
        String expected2 = "Can't connect to the database\n"+expected;          // connection to database too hard to tests
        String expected3 = expected2+"--  App may malfunction  --";             // expected2 and expected3 allow to pass regardless of database connection state
        if( ! errMsg.equals(expected) && ! errMsg.equals(expected2) && ! errMsg.equals(expected3)){
            throw new AssertionError("Error messages was :\n"+errMsg+"\n\nWhile expecting :\n"+expected+"\n\n");
        }
    }

}
