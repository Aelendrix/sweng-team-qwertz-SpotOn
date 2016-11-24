//
//package ch.epfl.sweng.spotOn.test.gui;
//
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//
//import org.junit.After;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import ch.epfl.sweng.spotOn.R;
//import ch.epfl.sweng.spotOn.gui.TabActivity;
//import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
//import ch.epfl.sweng.spotOn.test.util.TestInitUtils;
//
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
//import static android.support.test.espresso.action.ViewActions.typeText;
//import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.is;
//
//
///**
// * Created by Alexis Dewaele on 21/11/2016.
// */
//
//
//@RunWith(AndroidJUnit4.class)
//public class DrawTextActivityTest {
//
//    @Rule
//    public ActivityTestRule<TabActivity> mActivityRule = new ActivityTestRule<TabActivity>(TabActivity.class) {
//        @Override
//        public void beforeActivityLaunched(){
//            TestInitUtils.initContext();
//        }
//    };
//
//    @Test
//    public void sendTextCorrectly() {
//        onView(withText("Camera")).perform(click());
//        onView(withText("Add text")).perform(click());
//        onView(withId(R.id.textToDraw)).perform(typeText("Hello !")).perform(closeSoftKeyboard());
//        onView(withId(R.id.sendTextToDrawButton)).perform(click());
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivityRule.getActivity());
//        assertThat(preferences.getString("TD", ""), is("Hello !"));
//    }
//
//    @After
//    public void after(){
//        ConcreteLocationTracker.destroyInstance();
//        if( ConcreteLocationTracker.instanceExists()){
//            throw new AssertionError("DrawTextActivity : concreteLocationTracker mock instance not deleted");
//        }
//    }
//}