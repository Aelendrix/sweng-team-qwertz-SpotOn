package ch.epfl.sweng.spotOn.test.gui;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.widget.Toast;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;

/**
 * Created by quentin on 18.11.16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ToastProviderTest extends AndroidTestCase {

    Intent mEmtpyIntent = new Intent();
    Context mContext;

    @Rule
    public IntentsTestRule<TabActivity> intentsRule = new IntentsTestRule<TabActivity>(TabActivity.class){
        @Override
        public void beforeActivityLaunched(){
            MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
            LocalDatabase.initialize(mlt);
            ConcreteLocationTracker.setMockLocationTracker(mlt);
        }
    };

    @Test
    public void testSimpleToast() throws InterruptedException {
        //Intents.init();

//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                                     @Override
//                                                     public void run() {
//
//                                                     }
//                                                 }

//        ToastProvider.update(intentsRule.getActivity().getApplicationContext());
//        ToastProvider.printIfNoCurrent("nice toast !", Toast.LENGTH_LONG);

        Intents.release();
        Intents.init();

//        mContext = intentsRule.getActivity().getApplicationContext();
//        mContext = getContext();
//        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
//        mContext= InstrumentationRegistry.getContext();


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

//                mContext = getContext(); -> null
//                mContext = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext(); -> doesn't display'
//                mContext= InstrumentationRegistry.getContext();

                if(mContext==null){
                    throw new AssertionError("couldn't set context");
                }

                Toast.makeText(mContext, "test", Toast.LENGTH_LONG).show();
                Toast.makeText(mContext, "test", Toast.LENGTH_LONG).show();
                Toast.makeText(mContext, "test", Toast.LENGTH_LONG).show();

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    throw new AssertionError("coulnd't sleep");
                }
            }
        });

        Intents.release();

//        Toast.makeText(intentsRule.getActivity().getApplicationContext(), "test", Toast.LENGTH_LONG).show();
//
//        Thread.sleep(Toast.LENGTH_LONG / 2);
//
//        if (!ToastProvider.toastBeingDisplayed()) {
//            throw new AssertionError("Toast should be displayed");
//        }

        //Intents.release();
    }
}
