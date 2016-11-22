package ch.epfl.sweng.spotOn.test;

import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Toast;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.ImageAdapter;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.gui.ViewFullsizeImageActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ToastsProviderTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class, true, false) {
        @Override
        public void beforeActivityLaunched() {
            MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
            LocalDatabase.initialize(mlt);
            ConcreteLocationTracker.setMockLocationTracker(mlt);
            User.initializeFromFb("René", "Coty", "cestDoncTonAmi");
        }
    };
    private Intent displayFullSizeImageIntent;

    @Before
    public void initLocalDatabase() throws InterruptedException {
        displayFullSizeImageIntent = new Intent();
    }

    @Test
    public void launchFullPictureActivity() throws Exception {
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);

        ToastProvider.printOverCurrent("zboub",Toast.LENGTH_LONG);

        Thread.sleep(1000);


//        onView(withId(R.id.viewpager)).perform(clickXY(50, 50));
//        Thread.sleep(500);
//        onView(withId(R.id.upvoteButton)).perform(click());
//        Thread.sleep(500);
//        onView(withId(R.id.downvoteButton)).perform(click());
//        Thread.sleep(500);
//        onView(withId(R.id.reportButton)).perform(click());
    }
}
