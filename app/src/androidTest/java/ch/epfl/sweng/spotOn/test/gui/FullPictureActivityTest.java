 package ch.epfl.sweng.spotOn.test.gui;


import android.content.Intent;
import android.location.Location;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * Created by nico on 09.11.16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FullPictureActivityTest {


    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class,true,false);

    @Before
    public void initLocalDatabase(){

        Location location = new Location("testLocationProvider");
        location.setLatitude(46.52890355757567);
        location.setLongitude(6.569420238493345);
        location.setAltitude(0);
        location.setTime(System.currentTimeMillis());

        TestInitUtils.initContext(location);

        PhotoObject po = PhotoObjectTestUtils.paulVanDykPO();
        LocalDatabase.getInstance().addPhotoObject(po);

        LocalDatabase.getInstance().notifyListeners();
    }

    @Test
    public void launchFullPictureActivity() throws Exception{
        mActivityTestRule.launchActivity(new Intent());
        Thread.sleep(1000);
        onView(withId(R.id.viewpager)).perform(clickXY(50, 50));
        Thread.sleep(500);
        onView(withId(R.id.upvoteButton)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.upvoteButton)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.downvoteButton)).perform(click());
    }

    public static ViewAction clickXY(final int x, final int y){
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    @After
    public void after(){
        ConcreteLocationTracker.destroyInstance();
        if( ConcreteLocationTracker.instanceExists()){
            throw new AssertionError("FullPictureActivityTest : concreteLocationTracker mock instance not deleted : "+ConcreteLocationTracker.getInstance().getLocation());
        }
    }
}