package ch.epfl.sweng.project;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.firebase.database.DatabaseReference;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TabActivity extends AppCompatActivity implements MyStoriesFragment.OnFragmentInteractionListener, CameraFragment.OnFragmentInteractionListener, StoriesAroundMeFragment.OnFragmentInteractionListener {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    // The path to the root of the stored pictures Data in the database
    private final String PATH_TO_PICTURE_DATA = "pictureMetadata";
    //DB
    private LocalDatabase mDB = new LocalDatabase(PATH_TO_PICTURE_DATA);
    //TimerTask
    private final int TIME_BETWEEN_EXEC = 60*1000; //1 minutes
    private Timer mTimer;
    //task that will be run every x Time.
    private TimerTask mTimerTask = new TimerTask() {

        @Override
        public void run() {
            //refresh the local database every minutes
            //TODO: when the fragments are linked to this activity, move localisation service here and filter our localDB
            mDB.refresh();
            refreshMapMarkers();
        }
    };
    //will refresh the mapactivity fragments in function of the localDatabase
    private void refreshMapMarkers(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //start a looped runnable code every X minutes
        if(mTimer==null){
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(mTimerTask, 0, TIME_BETWEEN_EXEC);
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        //stop the timer
        mTimer.cancel();
        mTimer = null;
    }

    public void onFragmentInteraction(Uri uri) {

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyStoriesFragment(), "My Stories");
        adapter.addFragment(new CameraFragment(), "Camera");
        adapter.addFragment(new StoriesAroundMeFragment(), "Stories around me");
        viewPager.setAdapter(adapter);
    }

    /*
    This method uses the options menu when this activity is launched
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }
}
