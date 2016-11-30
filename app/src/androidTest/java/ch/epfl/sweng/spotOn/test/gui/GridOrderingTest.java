package ch.epfl.sweng.spotOn.test.gui;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;
import ch.epfl.sweng.spotOn.test.util.StorageRef_Test;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class GridOrderingTest {
    private PhotoObject po;
    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<TabActivity>(TabActivity.class){
        @Override
        public void beforeActivityLaunched(){
            po = PhotoObjectTestUtils.iceDivingPO();
            po.uploadWithoutFeedback();
            TestInitUtils.initContext();

        }
    };

    @Test
    public void testChangeOrdering () throws Exception{
        onView(withText("Around me")).perform(click());
        Thread.sleep(1000);
        onView(withText("Upvote")).perform(click());
        Thread.sleep(1000);
        onView(withText("Oldest")).perform(click());
        Thread.sleep(1000);
        onView(withText("Newest")).perform(click());
        Thread.sleep(1000);
        onView(withText("Hot")).perform(click());
    }

    @After
    public void clear(){
        DatabaseRef.deletePhotoObjectFromDB(po.getPictureId());
    }
}
