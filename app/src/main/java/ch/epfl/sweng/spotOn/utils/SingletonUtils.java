package ch.epfl.sweng.spotOn.utils;

import android.content.Context;
import android.location.LocationManager;

import ch.epfl.sweng.spotOn.FirebaseConnectionTracker.ConcreteFirebaseConnectionTracker;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationManagerWrapper;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.user.UserManager;

/**
 * Created by quentin on 22.12.16.
 *
 */

public class SingletonUtils {

    public static void initializeSingletons(Context c){
        ConcreteLocationTracker.initialize(new ConcreteLocationManagerWrapper((LocationManager) c.getSystemService(Context.LOCATION_SERVICE)));
        LocalDatabase.initialize(ConcreteLocationTracker.getInstance());
        UserManager.initialize();
        ServicesChecker.initialize(ConcreteLocationTracker.getInstance(), LocalDatabase.getInstance(), UserManager.getInstance(), ConcreteFirebaseConnectionTracker.getInstance());
    }
}
