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
import java.util.function.LongToDoubleFunction;

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
import ch.epfl.sweng.spotOn.utils.ServicesChecker;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ToastsProviderTest {

    private final static long sec1 = 1000;
    private final static long sec2 = 2*1000;
    private final static long sec3 = 3*1000;
    private final static long sec5 = 5*1000;
    private final static long sec10 = 10*1000;
    private final static long shortD = 2000;
    private final static long longD = 3500;

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class, true, false) {
        @Override
        public void beforeActivityLaunched() {
            MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
            LocalDatabase.initialize(mlt);
            ConcreteLocationTracker.setMockLocationTracker(mlt);
            User.initializeFromFb("René", "Coty", "cestDoncTonAmi");
            ServicesChecker.initialize(mlt, LocalDatabase.getInstance());
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

        assertNoDisplayedToast();

        ToastProvider.printOverCurrent("baseToast",Toast.LENGTH_LONG);
        ToastProvider.printAfterCurrent("toastAfterCurrent",Toast.LENGTH_LONG);
        assertSomeDisplayedToast();
        Thread.sleep(sec5);
        assertNoDisplayedToast();


        ToastProvider.printOverCurrent("baseToast",Toast.LENGTH_LONG);
        Thread.sleep(sec1);
        ToastProvider.printOverCurrent("ToastOverCurrent",Toast.LENGTH_LONG);
        assertSomeDisplayedToast();
        Thread.sleep(sec5);
        assertNoDisplayedToast();

        ToastProvider.printOverCurrent("baseToast",Toast.LENGTH_LONG);
        Thread.sleep(sec1);
        ToastProvider.printIfNoCurrent("ToastIfNoCurrent",Toast.LENGTH_LONG);
        assertSomeDisplayedToast();
        Thread.sleep(longD-sec1+50);
        assertNoDisplayedToast();

        ToastProvider.printIfNoCurrent("(there should have been no toast after this 'basetoast')",Toast.LENGTH_LONG);
        Thread.sleep(sec5);
        assertNoDisplayedToast();

        ToastProvider.printIfNoCurrent("ToastIfNoCurrent",Toast.LENGTH_LONG);
        Thread.sleep(sec1);
        assertSomeDisplayedToast();
        Thread.sleep(sec5);
        assertNoDisplayedToast();

        ToastProvider.printIfNoCurrent("finished", Toast.LENGTH_LONG);

        Thread.sleep(sec1);
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        Thread.sleep(sec1);
        onView(withId(R.id.viewpager)).perform(swipeRight());
    }


    private void assertNoDisplayedToast(String message){
        if(ToastProvider.toastBeingDisplayed()){
            throw new AssertionError(message);
        }
    }
    private void assertNoDisplayedToast(){
        assertNoDisplayedToast("Expected no toast displayed, found some");
    }

    private void assertSomeDisplayedToast(String message){
        if(!ToastProvider.toastBeingDisplayed()){
            throw new AssertionError(message);
        }
    }
    private void assertSomeDisplayedToast(){
        assertNoDisplayedToast("Expected some toast displayed, found none");
    }
}
