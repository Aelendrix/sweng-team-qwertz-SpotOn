package ch.epfl.sweng.spotOn.test;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Toast;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;
import ch.epfl.sweng.spotOn.utils.ToastProvider;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ToastsProviderTest {

    private final static long sec1 = 1000;
    private final static long sec2 = 2*1000;
    private final static long sec3 = 3*1000;
    private final static long sec5 = 5*1000;
    private final static long sec10 = 10*1000;
    private final static long shortD = 2000;    // duration of Toasts.LENGTH
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

    @After
    public void cleanUpDatabase(){
        DatabaseRef.deleteUserFromDB("cestDoncTonAmi");
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

        // printIfNoCurrent() shouldn't display toast
        ToastProvider.printOverCurrent("BaseToast", Toast.LENGTH_LONG);
        Thread.sleep(sec2);
        ToastProvider.printIfNoCurrent("(there should have been no toast after this 'baseToast')",Toast.LENGTH_LONG);
        Thread.sleep(longD - sec2 + 200 ); // 200 ms of margin of error since toast isn't displayed immediately
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

    @Test (expected = IllegalArgumentException.class)
    public void badlyUpdatedToastProviderThrowsException(){
        ToastProvider.update(null);
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
