package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
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
import ch.epfl.sweng.spotOn.test.util.LocalDatabaseUtils;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Created by Alexis Dewaele on 09/11/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewFullSizeImageActivityTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class,true,false);
    private Intent displayFullSizeImageIntent;

    @Before
    public void initLocalDatabase() throws InterruptedException {
        LocalDatabaseUtils.initLocalDatabase();
        displayFullSizeImageIntent = new Intent();
    }

    @Test
    public void launchFullPictureActivity() throws Exception{

        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
        //Let the local database refresh
        Thread.sleep(4000);
        //onView(withId(R.id.viewpager)).perform(clickXY(50, 50));
        onData(anything()).inAdapterView(withId(R.id.gridview)).atPosition(0).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.upvoteButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.downvoteButton)).perform(click());
        Thread.sleep(1000);
        //report
        onView(withId(R.id.reportButton)).perform(click());
        Thread.sleep(1000);
        //unreport
        onView(withId(R.id.reportButton)).perform(click());
        Thread.sleep(1000);
        /*
        //come back an reperform the action with an already downloaded picture
        mActivityTestRule.getActivity().onBackPressed();
        Thread.sleep(500);
        onView(withId(R.id.viewpager)).perform(clickXY(50, 50));
        Thread.sleep(500);
        */
    }



    @Test
    public void swipeBetweenPicturesTest() throws InterruptedException{
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
        Thread.sleep(4000);
        onView(withId(R.id.viewpager)).perform(clickXY(50, 50));
        Thread.sleep(2000);
        onView(withId(R.id.pager)).perform(swipeLeft());
        Thread.sleep(1000);
        onView(withId(R.id.pager)).perform(swipeRight());
        Thread.sleep(1000);
    }

    @Test
    public void buttonsDisappearTest() throws InterruptedException {
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
        Thread.sleep(4000);
        onView(withId(R.id.viewpager)).perform(clickXY(50, 50));
        Thread.sleep(1000);
        //make buttons disappear
        onView(withId(R.id.pager)).perform(click());
        Thread.sleep(1000);
        //make buttons reappear
        onView(withId(R.id.pager)).perform(click());
        Thread.sleep(1000);
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
    public void after() {
        LocalDatabaseUtils.afterTests();
    }
}
