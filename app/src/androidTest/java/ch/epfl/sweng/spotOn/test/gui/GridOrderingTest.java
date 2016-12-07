package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.location.Location;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;
import ch.epfl.sweng.spotOn.test.util.StorageRef_Test;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class GridOrderingTest {
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
    public void testChangeOrdering () throws Exception{
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
        Thread.sleep(1000);
        onView(withText("Around me")).perform(click());
        Thread.sleep(1000);
        onView(withText("Upvote")).perform(click());
        Thread.sleep(1000);
        onView(withText("Oldest")).perform(click());
        Thread.sleep(1000);
        onView(withText("Newest")).perform(click());
        Thread.sleep(1000);
        onView(withText("Hot")).perform(click());
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
