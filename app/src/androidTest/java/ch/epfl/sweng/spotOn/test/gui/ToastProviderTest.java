package ch.epfl.sweng.spotOn.test.gui;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;

/**
 * Created by quentin on 18.11.16.
 */

public class ToastProviderTest {

    @Rule
    public IntentsTestRule<TabActivity> intentsRule = new IntentsTestRule<TabActivity>(TabActivity.class){
        @Override
        public void beforeActivityLaunched(){
            MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
            LocalDatabase.initialize(mlt);
            ConcreteLocationTracker.setMockLocationTracker(mlt);
        }
    };

    @Test
    public void test
}
