package ch.epfl.sweng.spotOn.test.gui;

import android.graphics.Bitmap;
import android.location.LocationManager;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.FlakyTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.gui.ViewFullsizeImageActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.util.Pair;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Alexis Dewaele on 28/10/2016.
 */

@RunWith(AndroidJUnit4.class)
public class DisplayImageFromThumbnail {

    private List<Pair<Bitmap, String>> mThumbnails;

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

    @Before
    public void executeBefore() {
        mThumbnails = LocalDatabase.getThumbnailArray();
    }
    @Rule
    public ActivityTestRule<TabActivity> rule = new ActivityTestRule<>(TabActivity.class);

    @Test
    public void clickOnThumbnailTest() throws InterruptedException{
        Intents.init();
        Thread.sleep(3000);
        onView(withId(R.id.viewpager)).perform(clickXY(100, 100));
        //stopTiming(idlingResource);

        intended(hasComponent(ViewFullsizeImageActivity.class.getName()));
        /*if(mThumbnails.size() != 0) {
            Log.i("Thumbnail", "Clicked on thumbnail");
            intended(hasComponent(ViewFullsizeImageActivity.class.getName()));
        }*/
        Intents.release();
    }
}
