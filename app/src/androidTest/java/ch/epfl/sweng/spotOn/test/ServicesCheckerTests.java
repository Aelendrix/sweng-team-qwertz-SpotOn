package ch.epfl.sweng.spotOn.test;

import android.content.Intent;
import android.location.Location;
import android.support.test.rule.ActivityTestRule;

import org.junit.Assert;
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

import static org.hamcrest.Matchers.is;

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
        mMockUser = new MockUser_forTests("some","name","UUID", 0L,new HashMap<String, Long>(), true, true);
        UserManager.getInstance().setMockUser(mMockUser);

        ServicesChecker.allowDisplayingToasts(false);
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), new MockFirebaseConnectionTracker_forTests());
        }
    };

    @Test
    public void testCorrectErrorMessage() throws InterruptedException {

        mActivityTestRule.launchActivity(new Intent());

        String expected = "";

        // no correct user
        UserManager.getInstance().destroyUser();
        UserManager.getInstance().setEmptyUser();
        expected = "You're not logged in\n"+"--  Some features may be disabled  --";
        checkExpected(expected);
        expected = User.LOGIN_NOT_LOGGED_in_MESSAGE+"\n"+" --  Feature unavailable  -- ";
        checkExpected_takePictureErrorMessage(expected);
        expected = "You're not logged in";
        checkExpected_LoginErrorMessage(expected);

        // correct state
        UserManager.getInstance().destroyUser();
        UserManager.getInstance().setMockUser(mMockUser);
        expected = "";
        checkExpected(expected);
        if( ServicesChecker.getInstance().allServicesOk()){
            throw new AssertionError("should have no correct user");
        }
        if( ServicesChecker.getInstance().canSendToServer()){
            throw new AssertionError("should not be able to send to server");
        }
        if( ServicesChecker.getInstance().canTakePicture()){
            throw new AssertionError("should not be able to take a picture");
        }

        // no correct Location
        Location tempStoreLocation = mlt.getLocation();
        mlt.forceLocationTimeout();
        expected = "Can't localize your device\n"+"--  Some features may be disabled  --";
        checkExpected(expected);
        expected = "Can't find your GPS location\n"+" --  Feature unavailable  -- ";
        checkExpected_takePictureErrorMessage(expected);

        // restore location
        mlt.setMockLocation(tempStoreLocation);
    }

    @Test
    public void testProvideLoginErrorMessage() throws InterruptedException{
        String errorMessage = ServicesChecker.getInstance().provideLoginErrorMessage();
        Thread.sleep(1000);

        Assert.assertThat(errorMessage.equals(""), is(true));
    }

    @Test
    public void testTakePictureErrorMessage(){
        String errorMessage = ServicesChecker.getInstance().takePictureErrorMessage();

        Assert.assertThat(errorMessage.equals("takePictureErrorMessage : all good"), is(true));
    }

    @Test
    public void testSendToServerErrorMessage(){
        String errorMessage = ServicesChecker.getInstance().sendToServerErrorMessage();

        Assert.assertThat(errorMessage.equals("sendToServerErrorMessage : all good"), is(true));
    }

    @Test
    public void testDatabaseIsNotConnected(){
        boolean dbIsNotConnected = ServicesChecker.getInstance().databaseNotConnected();

        Assert.assertThat(dbIsNotConnected, is(false));
    }

    @Test
    public void testFirebaseDatabaseDisconnected(){
        ServicesChecker.getInstance().firebaseDatabaseDisconnected();
        boolean dbIsNotConnected = ServicesChecker.getInstance().databaseNotConnected();

        Assert.assertThat(dbIsNotConnected, is(true));
    }


// HELPERS
    private void checkExpected(String expected){
        String errMsg = ServicesChecker.getInstance().provideErrorMessage();
        if( ! errMsg.equals(expected)){
            throw new AssertionError("Error messages was :\n"+errMsg+"\n\nWhile expecting :\n"+expected+"\n\n");
        }
    }

    private void checkExpected_takePictureErrorMessage(String expected){
        String errMsg = ServicesChecker.getInstance().takePictureErrorMessage();
        if( ! errMsg.equals(expected)){
            throw new AssertionError("Error messages was :\n"+errMsg+"\n\nWhile expecting :\n"+expected+"\n\n");
        }
    }

    private void checkExpected_LoginErrorMessage(String expected){
        String errMsg = ServicesChecker.getInstance().provideLoginErrorMessage();
        if( ! errMsg.equals(expected)){
            throw new AssertionError("Error messages was :\n"+errMsg+"\n\nWhile expecting :\n"+expected+"\n\n");
        }
    }
}
