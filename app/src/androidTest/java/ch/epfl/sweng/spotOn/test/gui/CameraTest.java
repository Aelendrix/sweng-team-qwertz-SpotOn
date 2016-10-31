package ch.epfl.sweng.spotOn.test.gui;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static ch.epfl.sweng.spotOn.test.gui.ImageViewCatcher.hasDrawable;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.ActionBar;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;

/**
 * Created by Alexis Dewaele on 28/10/2016.
 * Test that the app can take pictures correctly
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CameraTest {
    /*
    @Rule
    public IntentsTestRule<TabActivity> intentsRule = new IntentsTestRule<>(TabActivity.class);

    @Before
    public void stubCameraIntent(){
        ActivityResult result = createImageCaptureStub();
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);
    }

    @Test
    public void testTakePhoto() {
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        onView(withId(R.id.image_view)).check(matches(not(hasDrawable())));

        onView(withId(R.id.captureButton)).perform(click());

        //onView(withId(R.id.image_view)).check(matches(hasDrawable()));
        Intents.release();
    }

    private ActivityResult createImageCaptureStub(){
        Bundle bundle = new Bundle();
        Bitmap icon = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        return new ActivityResult(Activity.RESULT_OK, resultData);
    }
    */
/*
    @Test
    public void takePictureTest() {
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        Bitmap icon = BitmapFactory.decodeResource(InstrumentationRegistry.getTargetContext().getResources(), R.mipmap.ic_launcher);

        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, resultData);
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);
        onView(withId(R.id.captureButton)).perform(click());
        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE));
    }
*/

    @Rule
    public IntentsTestRule<TabActivity> mRule = new IntentsTestRule<>(TabActivity.class);

    @Test
    public void captureTest() {
        onView(withText("Camera")).perform(click());
        Bitmap icon = BitmapFactory.decodeResource(InstrumentationRegistry.getTargetContext().getResources(), R.mipmap.ic_launcher);
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, resultData);
        intending(toPackage("com.android.camera2")).respondWith(result);
        onView(withId(R.id.captureButton)).perform(click());
    }
}
