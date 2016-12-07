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
import ch.epfl.sweng.spotOn.test.util.LocalDatabaseUtils;
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

    @Before
    public void initLocalDatabase() throws InterruptedException {
        LocalDatabaseUtils.initLocalDatabase();
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
        LocalDatabaseUtils.afterTests();
    }

}
