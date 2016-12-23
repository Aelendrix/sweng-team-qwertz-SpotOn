package ch.epfl.sweng.spotOn.test.gui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.view.View;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.gui.TakePictureFragment;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.utils.BitmapUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Nico
 * Test fakes the phone to take a picture, and then modifies the picture using the user UI.
 */
public class TestTakePictureFragment {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class) {
        @Override
        public void beforeActivityLaunched() {
            TestInitUtils.initContext();
        }
    };
    private Uri mImageToUploadUri;
    private File file;
    private String mActualPhotoObjectPictureId;

    @Test
    public void StoreFunctionWorking() throws Exception {
        onView(withText("Camera")).perform(click());
        //create a bitmap that will fake a picture taken by the camera of the phone
        final TakePictureFragment pictureFragment = (TakePictureFragment) mActivityTestRule.getActivity().getSupportFragmentManager().getFragments().get(1);
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut;
        Integer counter = 0;
        file = new File(path, "TestPicture" + counter + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        fOut = new FileOutputStream(file);

        Bitmap pictureBitmap = Bitmap.createBitmap(2000, 2000, Bitmap.Config.ARGB_8888); //po.getThumbnail();
        pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        fOut.flush(); // Not really required
        fOut.close(); // do not forget to close the stream
        mImageToUploadUri = BitmapUtils.getUriFromFile(pictureFragment.getContext(), file);

        mActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pictureFragment.processResult(mImageToUploadUri);

            }
        });
        //at this point we are in the tabActivity and the phone "took" a full black picture

        onView(withId(R.id.editButton)).perform(click());
        //in the editActivity button check
        onView(withId(R.id.addTextButton)).check(matches(isDisplayed()));
        onView(withId(R.id.rotateButton)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmButton)).check(matches(isDisplayed()));
        //modifiy the actual picture
        onView(withId(R.id.rotateButton)).perform(click());
        onView(withId(R.id.addTextButton)).perform(click());
        onView(withId(R.id.textToDraw)).perform(typeText("Hello !")).perform(closeSoftKeyboard());
        onView(withId(R.id.sendTextToDrawButton)).perform(click());
        onView(withId(R.id.activity_edit_picture)).perform(clickXY(500, 500));
        onView(withId(R.id.confirmButton)).perform(click());
        //in the tabActivity button are there check
        onView(withId(R.id.storeButton)).check(matches(isDisplayed()));
        onView(withId(R.id.sendButton)).check(matches(isDisplayed()));
        onView(withId(R.id.captureButton)).check(matches(isDisplayed()));
        onView(withId(R.id.editButton)).check(matches(isDisplayed()));
        //in the tabActivity and save and store the image
        onView(withId(R.id.storeButton)).perform(click());
        onView(withId(R.id.sendButton)).perform(click());
        mActualPhotoObjectPictureId = pictureFragment.getLastUploadedPictureId();
        Log.d("TestTest",mActualPhotoObjectPictureId);
        onView(withId(R.id.captureButton)).perform(click());
        Thread.sleep(5000);
        //cannot be put in the After method cause it's not working there
        DatabaseRef.deletePhotoObjectFromDB(mActualPhotoObjectPictureId);
        StorageRef.deletePictureFromStorage(mActualPhotoObjectPictureId);
    }

    @After
    public void after() throws InterruptedException{
        ConcreteLocationTracker.destroyInstance();
        if (ConcreteLocationTracker.instanceExists()) {
            throw new AssertionError("TakePictureFragmentTest : concreteLocationTracker mock instance not deleted");
        }
    }

    private ViewAction clickXY(final float x, final float y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;

                        return new float[]{screenX, screenY};
                    }
                },
                Press.FINGER);
    }
}