package ch.epfl.sweng.spotOn.test;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.gui.MainActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

/**
 * Created by nico on 23.11.16.
 * Those tests are just for the line coverage, real tests will come later when @sg.pepper finish his feature
 */

@RunWith(AndroidJUnit4.class)
public class ToastProviderMethodTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, false, false);

    @Before
    public void init(){
        ToastProvider.update(InstrumentationRegistry.getContext());
        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
        LocalDatabase.initialize(mlt);
        ConcreteLocationTracker.setMockLocationTracker(mlt);
        User.initializeFromFb("René", "Coty", "cestDoncTonAmi");
        ServicesChecker.initialize(mlt, LocalDatabase.getInstance());
        mActivityTestRule.launchActivity(new Intent());
    }

    @Test
    public void printTest() throws Exception{
        mActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(5000);
                    ToastProvider.printOverCurrent("yolo", ToastProvider.SHORT);
                    Thread.sleep(100);
                    ToastProvider.printAfterCurrent("yolo", ToastProvider.SHORT);
                    //Thread.sleep(ToastProvider.SHORT);
                    ToastProvider.printIfNoCurrent("yoloxD", ToastProvider.SHORT);
                    Thread.sleep(5000);
                    ToastProvider.printOverCurrent("finished", ToastProvider.SHORT);

                }
                catch(InterruptedException e)
                {
                }
            }
        });
    }

    @Test
    public void IllegalArgumentExceptionToast1() throws Exception{
        mActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    ToastProvider.printOverCurrent("yolo", 100);
                    throw new AssertionError("Toast try to print with a wrong dudration");
                }
                catch(IllegalArgumentException e)
                {

                }
            }
        });
    }
    @Test
    public void IllegalArgumentExceptionToast2() throws Exception{
        mActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    ToastProvider.printAfterCurrent("yolo", 100);
                    throw new AssertionError("Toast try to print with a wrong dudration");
                }
                catch(IllegalArgumentException e)
                {

                }
            }
        });
    }
    @Test
    public void IllegalArgumentExceptionToast3() throws Exception{
        mActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    ToastProvider.printIfNoCurrent("yolo", 100);
                    throw new AssertionError("Toast try to print with a wrong dudration");
                }
                catch(IllegalArgumentException e)
                {

                }
            }
        });
    }

}
