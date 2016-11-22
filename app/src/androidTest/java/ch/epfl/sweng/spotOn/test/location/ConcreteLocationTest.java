package ch.epfl.sweng.spotOn.test.location;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;

/**
 * Created by nico on 22.11.16.
 */
@RunWith(AndroidJUnit4.class)
public class ConcreteLocationTest {
    LocationTracker locationTracker;
    @Before
    public void init() {
        ConcreteLocationTracker.initialize(InstrumentationRegistry.getContext());
        locationTracker = ConcreteLocationTracker.getInstance();
    }
    @Test
    public void LocationTrackerTest(){
        locationTracker.hasValidLocation();
    }
}

