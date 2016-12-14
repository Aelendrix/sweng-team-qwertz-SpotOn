package ch.epfl.sweng.spotOn.test.gui;


import android.content.Intent;
import android.location.Location;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.gui.AboutPage;
import ch.epfl.sweng.spotOn.gui.SeePicturesFragment;
import ch.epfl.sweng.spotOn.gui.TabActivity;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.LocalDatabaseUtils;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;


/**
 * Created by nico on 09.11.16.
 */


@RunWith(AndroidJUnit4.class)
@LargeTest
public class FullPictureActivityTest {


    @Rule
    public ActivityTestRule<TabActivity> mActivityTestRule = new ActivityTestRule<>(TabActivity.class, true, false);

    @Before
    public void initLocalDatabase() throws InterruptedException{
        LocalDatabaseUtils.initLocalDatabase(true);
    }

    @Test
    public void launchFullPictureActivityAndVote() throws Exception{
        mActivityTestRule.launchActivity(new Intent());

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
    }

    @After
    public void after(){
        LocalDatabaseUtils.afterTests();
    }
}
