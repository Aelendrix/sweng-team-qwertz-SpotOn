package ch.epfl.sweng.project;

/**
 * Created by olivi on 10.10.2016.
 */
import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class PictureCaptureTest extends ActivityInstrumentationTestCase2<PictureActivity> {
    public PictureCaptureTest() {
        super(PictureActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

    public void testCanDisplayImage(){
        //onView(withID(R.id.captureButton)).perform(dispatchTakePictureIntent(this.findViewById(R.id.activity_picture)));
    }
}
