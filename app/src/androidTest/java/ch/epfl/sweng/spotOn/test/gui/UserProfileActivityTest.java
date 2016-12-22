package ch.epfl.sweng.spotOn.test.gui;

import android.support.annotation.NonNull;
import android.support.test.espresso.Espresso;
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
import ch.epfl.sweng.spotOn.gui.ViewUserPhotoActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.test.util.MockUser_forTests;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * created by Marie-Laure
 * In the userProfileActivity, test if you can view the full size image and the back button
 */
@RunWith(AndroidJUnit4.class)
public class UserProfileActivityTest {
    private PhotoObject po;
    @Rule
    public ActivityTestRule<UserProfileActivity> mActivityTestRule =
            new ActivityTestRule<UserProfileActivity>(UserProfileActivity.class)
            {
                @Override
                public void beforeActivityLaunched(){

                    final Object lock1 = new Object();
                    po = PhotoObjectTestUtils.germaynDeryckePO();
                    po.upload(true, new OnCompleteListener() {
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
                    h.put(po.getPictureId(), po.getCreatedDate().getTime());

                    User mockUser = new MockUser_forTests("julius","caius","Test", 1000, h, true, true);
                    TestInitUtils.initContextMockUser(mockUser);
                    ServicesChecker.getInstance().allowDisplayingToasts(false);
                    LocalDatabase.getInstance().addPhotoObject(po);
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
        //this need to be changed, because the pressback occured before going into the FullSizeImage view
        //Espresso.pressBack();
        Intents.release();
    }

    @Test
    public void deletePictureUserProfileActivity() {
        Intents.init();
        onView(withId(R.id.deletePictureButton)).perform(click());
        onView(withText("CANCEL")).perform(click());
        onView(withId(R.id.deletePictureButton)).perform(click());
        onView(withText("DELETE")).perform(click());
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
        DatabaseRef.deletePhotoObjectFromDB(po.getPictureId());
        StorageRef.deletePictureFromStorage(po.getPictureId());
    }
}
