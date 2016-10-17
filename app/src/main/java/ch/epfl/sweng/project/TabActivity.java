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

    // The path to the root of the stored key/value pairs in the daabase
    private final String PATH_TO_KEYVAL_ROOT = "pictureMetadata";

    // Firebase instance variables
    private DatabaseReference myDBref;
    //DB
    private LocalDatabase mDB = new LocalDatabase();
    //TimerTask
    private Timer mTimer;
    private TimerTask mTimerTask = new TimerTask() {

        @Override
        public void run() {
           refreshDB(mDB);
        }
    };
    //refresh the local database every minutes
    public void refreshDB(LocalDatabase DB){


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
        //start a looped runnable code every minutes

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
