package ch.epfl.sweng.spotOn.test.location;

import org.junit.Test;

import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;

/**
 * Created by quentin on 23.11.16.
 */

public class LocationTrackerTest_failureCases {

    @Test (expected = IllegalArgumentException.class)
    public void ExceptionWhenNotInitialized(){
        ConcreteLocationTracker.getInstance();
    }

    @Test (expected = IllegalArgumentException.class)
    public void gettersThrowExceptionsWhenUnitialized(){
        ConcreteLocationTracker.get();
    }

}
