package ch.epfl.sweng.spotOn.test.gui;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.MapFragment;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.gui.TakePictureFragment;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by nico on 16.11.16.
 */
@RunWith(AndroidJUnit4.class)
public class MapTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class);

    @Test
    public void refreshLocalisationMarker(){
            onView(withId(R.id.viewpager)).perform(swipeRight());
            onView(withId(R.id.viewpager)).perform(swipeLeft());
            onView(withId(R.id.viewpager)).perform(swipeLeft());
        final MapFragment mapFragment = (MapFragment) mActivityTestRule.getActivity().getSupportFragmentManager().getFragments().get(2);
        mapFragment.refreshMapLocation(new LatLng(0,0));

    }
}
