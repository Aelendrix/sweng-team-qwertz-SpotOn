package ch.epfl.sweng.spotOn.test.gui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.DrawTextActivity;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.gui.TakePictureFragment;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

/**
 * Created by Alexis Dewaele on 16/11/2016.
 */

@RunWith(AndroidJUnit4.class)
public class DrawTextActivityTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class) {
        @Override
        public void beforeActivityLaunched(){
            MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
            LocalDatabase.initialize(mlt);
            ConcreteLocationTracker.setMockLocationTracker(mlt);
        }
    };

    @Test
    public void getTextRight() {
        if(!LocalDatabase.instanceExists()){
            throw new AssertionError("LocalDatabase incorrectly initialized");
        }
        onView(withText("Camera")).perform(click());
        onView(withText("Add Text")).perform(click());
        onView(withId(R.id.textToDraw)).perform(typeText("Hello !")).perform(closeSoftKeyboard());
        onView(withId(R.id.sendTextToDrawButton)).perform(click());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivityTestRule.getActivity());
        assertThat(preferences.getString("TD", ""), is("Hello !"));
    }

}
