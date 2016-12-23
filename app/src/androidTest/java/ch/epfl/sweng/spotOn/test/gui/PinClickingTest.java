package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.test.util.LocalDatabaseTestUtils;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by olivi on 18.12.2016.
 *
 */

public class PinClickingTest {

    private PhotoObject newPo;
    private ArrayList<String> titlesOfPhotos;

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class,true,false);

    @Before
    public void initLocalDatabase() throws InterruptedException {
        LocalDatabaseTestUtils.initLocalDatabase(false);

        //Add another photo to the database -> yellow pin on map
        final Object lock1 = new Object();
        newPo = PhotoObjectTestUtils.farAwayPicture();

        newPo.upload(true, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.getException()!=null){
                    lock1.notify();
                    throw new IOError(new IOException("LocalDatabaseTestUtils : ERROR - uploading first testPhotoObject failed"));
                }else{
                    synchronized (lock1){
                        lock1.notify();
                    }
                }
            }
        });
        synchronized (lock1)
        {lock1.wait();}
        LocalDatabase.getInstance().addPhotoObject(newPo);
        //3 elements only in LocalDatabase
        titlesOfPhotos = LocalDatabase.getInstance().getTitlesOfPictures();
    }

    @Test
    public void clickOnPinTest() throws Exception{
        mActivityTestRule.launchActivity(new Intent());
        onView(withText(R.string.tab_map)).check(matches(isDisplayed()));
        onView(withText(R.string.tab_map)).perform(click());
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        //Picture ID of the first element in the localDatabase
        String markerTitle1 = titlesOfPhotos.get(0);
        String markerTitle2 = titlesOfPhotos.get(1);
        String markerTitle3 = titlesOfPhotos.get(2);
        UiObject marker = device.findObject(new UiSelector().descriptionContains(markerTitle1));
        marker.click();
        UiObject marker2 = device.findObject(new UiSelector().descriptionContains(markerTitle2));
        marker2.click();
        UiObject marker3 = device.findObject(new UiSelector().descriptionContains(markerTitle3));
        marker3.click();
    }

    @After
    public void after() {
        if (LocalDatabase.instanceExists()) {
            LocalDatabaseTestUtils.afterTests();
            if (newPo != null) {
                DatabaseRef.deletePhotoObjectFromDB(newPo.getPictureId());
                StorageRef.deletePictureFromStorage(newPo.getPictureId());
            }
        }
    }
}
