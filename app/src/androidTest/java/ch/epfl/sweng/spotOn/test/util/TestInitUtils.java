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
        if(UserManager.instanceExists()){
            UserManager.getInstance().destroyUser();
        }
        UserManager.initialize();
        UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest(location);
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());

    }

    public static void initContext(double latitude, double longitude){
        if(UserManager.instanceExists()){
            UserManager.getInstance().destroyUser();
        }
        UserManager.initialize();
        UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");

        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest(latitude, longitude);
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);

        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
    }

    public static void initContext(){   // same with MockLocationTracker default location
        if(UserManager.instanceExists()){
            UserManager.getInstance().destroyUser();
        }
        UserManager.initialize();
        UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
    }

    public static void initContextServicesCheckSilent(){
        if(UserManager.instanceExists()){
            UserManager.getInstance().destroyUser();
        }
        UserManager.initialize();
        UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);

        ServicesChecker.allowDisplayingToasts(false);
        ServicesChecker.initialize(mlt, LocalDatabase.getInstance(), UserManager.getInstance(), new MockFirebaseConnectionTracker_forTests());
    }

    public static void initContextNoServicesChecks(double latitude, double longitude){
        if(UserManager.instanceExists()){
            UserManager.getInstance().destroyUser();
        }
        UserManager.initialize();
        UserManager.getInstance().setUserFromFacebook("Sweng", "Sweng", "114110565725225");
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest(latitude, longitude);
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
    }

    public static void initContextMockUser(User user){   // same with MockLocationTracker default location
        UserManager.initialize();
        UserManager.getInstance().setMockUser(user);
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);

        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
    }

    public static void initContextNoUser(Location location){
        if(UserManager.instanceExists()){
            UserManager.getInstance().destroyUser();
        }
        UserManager.initialize();
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest(location);
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
    }

    public static void initContextNoUser(){
        if(UserManager.instanceExists()){
            UserManager.getInstance().destroyUser();
        }
        UserManager.initialize();
        // destroy LocationTrackerSingleton if need be
        if(ConcreteLocationTracker.instanceExists()){
            ConcreteLocationTracker.destroyInstance();
        }

        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
        ConcreteLocationTracker.setMockLocationTracker(mlt);

        LocalDatabase.initialize(mlt);
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
    }
}
