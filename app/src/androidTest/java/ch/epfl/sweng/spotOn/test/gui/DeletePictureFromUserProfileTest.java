package ch.epfl.sweng.spotOn.test.gui;

import android.support.annotation.NonNull;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Alexis on 22/12/2016.
 *
 */

@RunWith(AndroidJUnit4.class)
public class DeletePictureFromUserProfileTest {
    private PhotoObject po1;

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



                    User mockUser = new MockUser_forTests("julius","caius","Test", 1000, h, true, true);
                    TestInitUtils.initContextMockUser(mockUser);
                    ServicesChecker.getInstance().allowDisplayingToasts(false);
                    LocalDatabase.getInstance().addPhotoObject(po1);
                }
            };

    @Test
    public void deletePictureUserProfileActivity() {
        Intents.init();
        onView(withId(R.id.deletePictureButton)).perform(click());
        onView(withText("Cancel")).perform(click());
        onView(withId(R.id.deletePictureButton)).perform(click());
        onView(withText("Delete")).perform(click());
        Intents.release();
    }


    @After
    public void clearObject() {
        DatabaseRef.deletePhotoObjectFromDB(po1.getPictureId());
        StorageRef.deletePictureFromStorage(po1.getPictureId());
    }
}
