package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.TabActivity;

import ch.epfl.sweng.spotOn.test.util.LocalDatabaseTestUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class GridOrderingTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class,true,false);

    @Before
    public void initLocalDatabase() throws InterruptedException  {
        LocalDatabaseTestUtils.initLocalDatabase(false);
    }

    @Test
    public void testChangeOrdering () throws Exception{
        mActivityTestRule.launchActivity(new Intent());
        //Let the local database refresh and click on the right tab
        onView(withText("Around me")).perform(click());
        //extend the list and close
        onView(withId(R.id.extend_list_button)).perform(click());
        onView(withId(R.id.extend_list_button)).perform(click());
        //order by upvote
        onView(withId(R.id.extend_list_button)).perform(click());
        onView(withId(R.id.order_upvote_button)).perform(click());
        //order by oldest
        onView(withId(R.id.extend_list_button)).perform(click());
        onView(withId(R.id.order_oldest_button)).perform(click());
        //order by newest
        onView(withId(R.id.extend_list_button)).perform(click());
        onView(withId(R.id.order_newest_button)).perform(click());
        //order by oldest
        onView(withId(R.id.extend_list_button)).perform(click());
        onView(withId(R.id.order_hottest_button)).perform(click());

    }

    @After
    public void after(){
        LocalDatabaseTestUtils.afterTests();
    }
}
