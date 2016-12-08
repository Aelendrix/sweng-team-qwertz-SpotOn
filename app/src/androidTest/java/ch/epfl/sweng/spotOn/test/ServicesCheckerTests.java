package ch.epfl.sweng.spotOn.test;

import android.content.Intent;
import android.location.Location;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.util.MockFirebaseConnectionTracker_forTests;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.test.util.MockUser_forTests;
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

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class){
        @Override
        public void beforeActivityLaunched(){

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

        ServicesChecker.allowDisplayingToasts(false);
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), new MockFirebaseConnectionTracker_forTests());
        }
    };

    @Test
    public void testCorrectErrorMessage() throws InterruptedException {

        mActivityTestRule.launchActivity(new Intent());

        Thread.sleep(500);

        String expected = "";


        // no correct user
        UserManager.getInstance().destroyUser();
        UserManager.getInstance().setEmptyUser();
        expected = "You're not logged in\n"+"--  Some features will be restricted  --";
        checkExpected(expected);

        // correct state
        UserManager.getInstance().destroyUser();
        UserManager.getInstance().setMockUser(mMockUser);
        expected = "";
        checkExpected(expected);

        // no correct Location
        Location tempStoreLocation = mlt.getLocation();
        mlt.forceLocationTimeout();
        expected = "Can't localize your device\n"+"--  Some features will be restricted  --";
        checkExpected(expected);

        // restore location
        mlt.setMockLocation(tempStoreLocation);

        Thread.sleep(5000);
    }

    private void checkExpected(String expected){
        String errMsg = ServicesChecker.getInstance().provideErrorMessage();
        if( ! errMsg.equals(expected)){
            throw new AssertionError("Error messages was :\n"+errMsg+"\n\nWhile expecting :\n"+expected+"\n\n");
        }
    }

}
