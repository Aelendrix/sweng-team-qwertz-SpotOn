package ch.epfl.sweng.spotOn.test.util;

import android.location.Location;


import ch.epfl.sweng.spotOn.FirebaseConnectionTracker.ConcreteFirebaseConnectionTracker;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;

/**
 * Created by quentin on 23.11.16.
 */

public class TestInitUtils {



    public static void initContext(Location location){
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest(location);
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        UserManager.initialize();
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
        UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");
    }

    public static void initContext(double latitude, double longitude){
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest(latitude, longitude);
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        UserManager.initialize();
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
        UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");
    }

    public static void initContextNoServicesChecks(double latitude, double longitude){
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest(latitude, longitude);
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        UserManager.initialize();
        UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");
    }

    public static void initContext(){   // same with MockLocationTracker default location
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        UserManager.initialize();
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
        UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");
    }

    public static void initContextMockUser(User user){   // same with MockLocationTracker default location
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        UserManager.initialize();
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
        UserManager.getInstance().setMockUser(user);
    }

    public static void initContextNoUser(Location location){
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest(location);
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        UserManager.initialize();
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
    }

    public static void initContextNoUser(){
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        UserManager.initialize();
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
    }
}
