package ch.epfl.sweng.spotOn.test.gui;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * Tests used to try the gui interaction in the pager of full size images
 * Created by Alexis Dewaele,  Nicolas Brunner  on 09/11/2016.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewFullSizeImageActivityTest {

    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class,true,false);

    @Before
    public void initLocalDatabase() throws InterruptedException {
        LocalDatabaseTestUtils.initLocalDatabase(false);
    }

    @Test
    public void swipeBetweenPicturesTest() throws InterruptedException{
        mActivityTestRule.launchActivity(new Intent());
        //go to the pager from the first grid item click
        onData(anything()).inAdapterView(withId(R.id.gridview)).atPosition(0).perform(click());
        //swipe between the page
        onView(withId(R.id.pager)).perform(swipeLeft());
        onView(withId(R.id.pager)).perform(swipeRight());
    }

    @Test
    public void buttonsDisappearTest() throws InterruptedException {
        mActivityTestRule.launchActivity(new Intent());
        //got to the pager from the first grid item click
        onData(anything()).inAdapterView(withId(R.id.gridview)).atPosition(0).perform(click());
        //make buttons disappear
        onView(withId(R.id.pager)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.upvoteButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.downvoteButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.reportButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        //make buttons reappear
        onView(withId(R.id.pager)).perform(click());
        Thread.sleep(500);
        onView(withId(R.id.upvoteButton)).check(matches(isDisplayed()));
        onView(withId(R.id.downvoteButton)).check(matches(isDisplayed()));
        onView(withId(R.id.reportButton)).check(matches(isDisplayed()));
    }

    @After
    public void after() {
        LocalDatabaseTestUtils.afterTests();
    }
}
