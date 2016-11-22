package ch.epfl.sweng.spotOn.test.gui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.content.FileProvider;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import ch.epfl.sweng.spotOn.BuildConfig;
import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.gui.TakePictureFragment;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;
import ch.epfl.sweng.spotOn.user.User;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


public class TestTakePictureFragment {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class){
        @Override
        public void beforeActivityLaunched(){
            MockLocationTracker_forTest mlt = new MockLocationTracker_forTest();
            LocalDatabase.initialize(mlt);
            ConcreteLocationTracker.setMockLocationTracker(mlt);
            User.initializeFromFb("","","test");
        }
    };
    Uri mImageToUploadUri;

    @Test
    public void StoreFunctionWorking() throws Exception{
        onView(withId(R.id.viewpager)).perform(swipeRight());
        onView(withId(R.id.viewpager)).perform(swipeRight());
        PhotoObject po = PhotoObjectTestUtils.paulVanDykPO();
        Thread.sleep(1000);
        final TakePictureFragment pictureFragment = (TakePictureFragment) mActivityTestRule.getActivity().getSupportFragmentManager().getFragments().get(1);
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut;
        Integer counter = 0;
        File file = new File(path, "TestPicture"+counter+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        fOut = new FileOutputStream(file);

        Bitmap pictureBitmap = Bitmap.createBitmap(2000, 2000, Bitmap.Config.ARGB_8888); //po.getThumbnail();
        pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        fOut.flush(); // Not really required
        fOut.close(); // do not forget to close the stream

        //mImageToUploadUri = Uri.fromFile(file);
        if(Build.VERSION.SDK_INT <= 23) {
            mImageToUploadUri = Uri.fromFile(file);
            Log.d("URI ImageUpload", mImageToUploadUri.toString());
        } else {
            //For API >= 24 (was the cause of the crash)
            mImageToUploadUri = FileProvider.getUriForFile(pictureFragment.getContext(),
                    BuildConfig.APPLICATION_ID + ".provider", file);
            Log.d("URI ImageUpload", mImageToUploadUri.toString());
        }

        mActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pictureFragment.processResult(mImageToUploadUri);

            }
        });
        onView(withId(R.id.viewpager)).perform(swipeLeft());
        Thread.sleep(1000);
        onView(withId(R.id.rotateButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.storeButton)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.sendButton)).perform(click());
        Thread.sleep(1000);

        /*

        // Mock up an ActivityResult:
        Intent returnIntent = new Intent();
        returnIntent.putExtra(10,10,mImageToUploadUri);
        Instrumentation.ActivityResult activityResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        // Create an ActivityMonitor that catch ChildActivity and return mock ActivityResult:
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(, activityResult , true);

        */
    }
}
