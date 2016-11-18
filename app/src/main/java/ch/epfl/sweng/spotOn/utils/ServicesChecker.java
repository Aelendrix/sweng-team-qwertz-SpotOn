package ch.epfl.sweng.spotOn.utils;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;

/**
 * Created by quentin on 17.11.16.
 */

public class ServicesChecker {

    public static boolean statusIsOk(){
        if( ! ConcreteLocationTracker.instanceExists() ||
                ! LocalDatabase.instanceExists() ||)
    }

    public String provideErrorMessage

}
