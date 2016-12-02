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
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
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
    private PhotoObject po;
    private PhotoObject secondPo;

    @Before
    public void initLocalDatabase() throws InterruptedException {
        Location location = new Location("testLocationProvider");
        location.setLatitude(46.52890355757567);
        location.setLongitude(6.569420238493345);
        location.setAltitude(0);
        location.setTime(System.currentTimeMillis());

        TestInitUtils.initContext(location);

        po = PhotoObjectTestUtils.germaynDeryckePO();
        LocalDatabase.getInstance().addPhotoObject(po);

        secondPo = PhotoObjectTestUtils.paulVanDykPO();
        LocalDatabase.getInstance().addPhotoObject(secondPo);

        LocalDatabase.getInstance().notifyListeners();

        displayFullSizeImageIntent = new Intent();

    }

    @Test
    public void launchFullPictureActivity() throws Exception{

        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
            //Thread.sleep(1000);
            //onView(withId(R.id.viewpager)).perform(clickXY(150, 50));
            onData(anything()).inAdapterView(withId(R.id.gridview)).atPosition(0).perform(click());
            //Thread.sleep(5000);
            onView(withId(R.id.upvoteButton)).perform(click());
            //Thread.sleep(1000);
            onView(withId(R.id.downvoteButton)).perform(click());
            //Thread.sleep(5000); // should permit to explore the "Karma" code
            onView(withId(R.id.reportButton)).perform(click());
            //Thread.sleep(1000);
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
        Thread.sleep(1000);
        onView(withId(R.id.viewpager)).perform(clickXY(50, 50));
        Thread.sleep(1000);
        onView(withId(R.id.pager)).perform(swipeLeft());
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
            throw new AssertionError("CameraTest : concreteLocationTracker mock instance not deleted : "+ConcreteLocationTracker.getInstance().getLocation());
        }
        // stuff below seems pretty unimportant to me
        if(LocalDatabase.instanceExists()){
            LocalDatabase ldb = LocalDatabase.getInstance();
            if(po!=null){
                ldb.removePhotoObject( po.getPictureId());
            }
            if(secondPo!=null){
                ldb.removePhotoObject(secondPo.getPictureId());
            }
        }
    }
}
