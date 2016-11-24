package ch.epfl.sweng.spotOn.test.gui;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static ch.epfl.sweng.spotOn.test.gui.TestImageViewCatcher.hasDrawable;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.user.UserManager;

/**
 * Created by Alexis Dewaele on 28/10/2016.
 * Test that the app can take pictures correctly
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CameraTest{

    private UserManager user = null;
    @Rule
    public IntentsTestRule<TabActivity> intentsRule = new IntentsTestRule<TabActivity>(TabActivity.class){
        @Override
        public void beforeActivityLaunched(){
            TestInitUtils.initContext();
        }
    };

    @Before
    public void stubCameraIntent() {
        UserManager user = UserManager.getInstance();
        if(!LocalDatabase.instanceExists()){
            throw new AssertionError("LocalDatabase incorrectly initialized");
        }
        ActivityResult result = createImageCaptureStub();
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);
    }

    @Test
    public void testTakePhoto() {
        if(!LocalDatabase.instanceExists()){
            throw new AssertionError("LocalDatabase incorrectly initialized");
        }

        onView(withText("Camera")).perform(click());
        onView(withId(R.id.image_view)).check(matches(not(hasDrawable())));

        onView(withId(R.id.captureButton)).perform(click());

        //onView(withId(R.id.image_view)).check(matches(hasDrawable()));
        //Test all behavior: before and after rotating picture
        onView(withId(R.id.storeButton)).perform(click());
        // should fix this
        // onView(withId(R.id.sendButton)).perform(click());
        onView(withId(R.id.rotateButton)).perform(click());
        onView(withId(R.id.storeButton)).perform(click());
        // should fix this
        // onView(withId(R.id.sendButton)).perform(click());
    }

    @Test
    public void addTextToPhotoTest() {
        if(!LocalDatabase.instanceExists()){
            throw new AssertionError("LocalDatabase incorrectly initialized");
        }

        onView(withText("Camera")).perform(click());
        onView(withText("Add text")).perform(click());
        onView(withId(R.id.textToDraw)).perform(typeText("Hello")).perform(closeSoftKeyboard());
        onView(withId(R.id.sendTextToDrawButton)).perform(click());
        onView(withId(R.id.captureButton)).perform(click());

        onView(withText("Add text")).perform(click());
        onView(withId(R.id.textToDraw)).perform(typeText("How are you ?")).perform(closeSoftKeyboard());
        onView(withId(R.id.sendTextToDrawButton)).perform(click());
        onView(withId(R.id.captureButton)).perform(click());
    }

    private ActivityResult createImageCaptureStub() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", BitmapFactory.decodeResource(intentsRule.getActivity().getResources(), R.mipmap.ic_launcher));

        Intent resultData = new Intent();
        resultData.putExtras(bundle);
        return new ActivityResult(Activity.RESULT_OK, resultData);
    }

    @After
    public void after(){
        ConcreteLocationTracker.destroyInstance();
        if( ConcreteLocationTracker.instanceExists()){
            throw new AssertionError("CameraTest : concreteLocationTracker mock instance not deleted : "+ConcreteLocationTracker.getInstance().getLocation());
        }
    }
}