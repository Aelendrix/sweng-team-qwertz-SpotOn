package ch.epfl.sweng.spotOn.test.gui;


import android.content.Intent;
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
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.test.util.LocalDatabaseTestUtils;
import ch.epfl.sweng.spotOn.user.UserManager;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;


/**
 * In a full sized image activity, test the user interface (upVote and downVote)
 */


@RunWith(AndroidJUnit4.class)
@LargeTest
public class FullPictureActivityTest {


    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class, true, false);

    @Before
    public void initLocalDatabase() throws InterruptedException{
        LocalDatabaseTestUtils.initLocalDatabaseMockUser(true);
    }

    @Test
    public void launchFullPictureActivityAndVote() throws Exception{
        mActivityTestRule.launchActivity(new Intent());
        if(!UserManager.getInstance().isLogInThroughFacebook() || !UserManager.getInstance().userIsLoggedIn()){
            throw new AssertionError("User not logged in, need to be logged-in for this test");
        }
        onView(withId(R.id.extend_list_button)).perform(click());
        onView(withId(R.id.order_newest_button)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.gridview)).atPosition(0).perform(click());
        //upvote and cancel the upvote
        onView(withId(R.id.upvoteButton)).perform(click());
        onView(withId(R.id.upvoteButton)).perform(click());
        //downvote and cancel the downvote
        onView(withId(R.id.downvoteButton)).perform(click());
        onView(withId(R.id.downvoteButton)).perform(click());
        //upvote then downvote
        onView(withId(R.id.upvoteButton)).perform(click());
        onView(withId(R.id.downvoteButton)).perform(click());
        //downvote then upvote
        onView(withId(R.id.downvoteButton)).perform(click());
        onView(withId(R.id.upvoteButton)).perform(click());
        onView(withId(R.id.reportButton)).perform(click());
        onView(withText("Cancel")).perform(click());
        onView(withId(R.id.reportButton)).perform(click());
        onView(withText("Report")).perform(click());
    }

    @After
    public void after(){
        LocalDatabaseTestUtils.afterTests();
    }
}
