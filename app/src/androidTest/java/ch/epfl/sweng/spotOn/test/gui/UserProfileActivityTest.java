package ch.epfl.sweng.spotOn.test.gui;

import android.support.annotation.NonNull;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.UserProfileActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.test.util.MockUser_forTests;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;

import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * created by Marie-Laure
 * In the userProfileActivity, test if you can view the full size image and the back button
 */
@RunWith(AndroidJUnit4.class)
public class UserProfileActivityTest {
    private PhotoObject po1;
    private PhotoObject po2;

    @Rule
    public ActivityTestRule<UserProfileActivity> mActivityTestRule =
            new ActivityTestRule<UserProfileActivity>(UserProfileActivity.class)
            {
                @Override
                public void beforeActivityLaunched(){

                    //Add 1st photoObject
                    final Object lock1 = new Object();
                    po1 = PhotoObjectTestUtils.germaynDeryckePO();
                    po1.upload(true, new OnCompleteListener() {
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
                    try {
                        synchronized (lock1) {
                            lock1.wait();
                        }
                    }
                    catch(InterruptedException e)
                    {
                        throw new AssertionError("Interrupted Exception");
                    }
                    HashMap<String, Long> h = new HashMap<>();
                    h.put(po1.getPictureId(), po1.getCreatedDate().getTime());

                    //Add 2nd photoObject
                    final Object lock2 = new Object();
                    po2 = PhotoObjectTestUtils.iceDivingPO();
                    po2.upload(true, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.getException()!=null){
                                lock2.notify();
                                throw new IOError(new IOException("LocalDatabaseTestUtils : ERROR - uploading first testPhotoObject failed"));
                            }else{
                                synchronized (lock2){
                                    lock2.notify();
                                }
                            }
                        }
                    });
                    try {
                        synchronized (lock2) {
                            lock2.wait();
                        }
                    }
                    catch(InterruptedException e)
                    {
                        throw new AssertionError("Interrupted Exception");
                    }
                    h.put(po2.getPictureId(), po2.getCreatedDate().getTime());

                    User mockUser = new MockUser_forTests("julius","caius","Test", 1000, h, true, true);
                    TestInitUtils.initContextMockUser(mockUser);
                    ServicesChecker.getInstance().allowDisplayingToasts(false);
                    LocalDatabase.getInstance().addPhotoObject(po1);
                    LocalDatabase.getInstance().addPhotoObject(po2);
                }
            };

    @Test
    public void testPressBackButton(){
        Intents.init();
        final UserProfileActivity userProfileActivity = mActivityTestRule.getActivity();
        userProfileActivity.runOnUiThread(new Runnable() {
            public void run() {
                userProfileActivity.onBackPressed();
            }
        });
        Intents.release();
    }

    @Test
    public void startViewUserPhotoActivity(){
        Intents.init();
        onView(withId(R.id.profilePicturesListView)).perform(clickXY(100,40));
        //intended(hasComponent(ViewUserPhotoActivity.class.getName()));
        //this need to be changed, because the pressBack occurred before going into the FullSizeImage view
        //Espresso.pressBack();
        Intents.release();
    }

    public ViewAction clickXY(final int x, final int y){
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {
                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);
                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        return new float[] {screenX, screenY};
                    }
                },
                Press.FINGER);
    }

    @After
    public void clearObject(){
        DatabaseRef.deletePhotoObjectFromDB(po1.getPictureId());
        StorageRef.deletePictureFromStorage(po1.getPictureId());

        DatabaseRef.deletePhotoObjectFromDB(po2.getPictureId());
        StorageRef.deletePictureFromStorage(po2.getPictureId());
    }
}
