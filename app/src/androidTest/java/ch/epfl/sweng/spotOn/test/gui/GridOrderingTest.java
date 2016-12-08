package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.test.util.LocalDatabaseUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class GridOrderingTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class,true,false);
    private Intent displayFullSizeImageIntent;

    @Before
    public void initLocalDatabase() throws InterruptedException  {
        LocalDatabaseUtils.initLocalDatabase();
        displayFullSizeImageIntent = new Intent();
    }

    @Test
    public void testChangeOrdering () throws Exception{
        mActivityTestRule.launchActivity(displayFullSizeImageIntent);
        //Let the local database refresh
        Thread.sleep(5000);
        onView(withText("Around me")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.extend_list_button)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.extend_list_button)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.extend_list_button)).perform(click());
        Thread.sleep(1000);
        onView(withText("Upvote")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.extend_list_button)).perform(click());
        Thread.sleep(1000);
        onView(withText("Oldest")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.extend_list_button)).perform(click());
        Thread.sleep(1000);
        onView(withText("Newest")).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.extend_list_button)).perform(click());
        Thread.sleep(1000);
        onView(withText("Hot")).perform(click());
    }

    @After
    public void after(){
        LocalDatabaseUtils.afterTests();
    }

}
