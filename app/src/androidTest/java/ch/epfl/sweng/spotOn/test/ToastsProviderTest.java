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

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.location.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

import static android.support.test.espresso.Espresso.onView;
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
    private final static long shortD = 2000;    // duration of Toasts.LENG
    private final static long longD = 3500;

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class, true, false);
    private Intent displayFullSizeImageIntent;

    @Before
    public void initLocalDatabase() throws InterruptedException {MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
        LocalDatabase.initialize(mlt);
        ConcreteLocationTracker.setMockLocationTracker(mlt);
        UserManager.initialize();
        UserManager.getInstance().setUserFromFacebook("René", "Coty", "cestDoncTonAmi");
        ServicesChecker.initialize(mlt, LocalDatabase.getInstance(), UserManager.getInstance());
        displayFullSizeImageIntent = new Intent();
    }



// TESTS

    @Test
    public void launchFullPictureActivity() throws Exception {
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);

        assertNoDisplayedToast();

        // single toasts ets displayed for 3.5 seconds
        ToastProvider.printOverCurrent("baseToast",Toast.LENGTH_LONG);
        Thread.sleep(200);
        assertSomeDisplayedToast();
        Thread.sleep(sec2);
        assertSomeDisplayedToast();
        Thread.sleep(sec5);
        assertNoDisplayedToast();

        // second toast takes over and extends the duration of baseToast
        ToastProvider.printOverCurrent("baseToast",Toast.LENGTH_LONG);
        Thread.sleep(sec2);
        ToastProvider.printOverCurrent("ToastOverCurrent",Toast.LENGTH_LONG);
        Thread.sleep(100);
        assertSomeDisplayedToast();
        Thread.sleep(sec2);
        assertSomeDisplayedToast();
        Thread.sleep(sec5);
        assertNoDisplayedToast();

        // printIfNoCurrent() displays single toast
        ToastProvider.printIfNoCurrent("ToastIfNoCurrent",Toast.LENGTH_LONG);
        Thread.sleep(sec2);
        assertSomeDisplayedToast();
        Thread.sleep(sec5);
        assertNoDisplayedToast();

        // printIfNoCurrent() shouldn't dispaly toast
        ToastProvider.printOverCurrent("BaseToast", ToastProvider.LONG);
        Thread.sleep(sec2);
        ToastProvider.printIfNoCurrent("(there should have been no toast after this 'basetoast')",Toast.LENGTH_LONG);
        Thread.sleep(longD - sec2 + 200 ); // 200 ms of margin of error since toast isn't displayed immediatly
        assertNoDisplayedToast();
        Thread.sleep(sec3);

        ToastProvider.printIfNoCurrent("finished", Toast.LENGTH_LONG);

        Thread.sleep(sec1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void invalidDurationToast_printOverCurrent(){
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
        ToastProvider.printOverCurrent("hello",100);
    }

    @Test (expected = IllegalArgumentException.class)
    public void invalidDurationToast_printIfNoCurrent(){
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
        ToastProvider.printIfNoCurrent("hello",100);
    }



// PRIVATE HELPERS

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
        assertSomeDisplayedToast("Expected some toast displayed, found none");
    }
}
