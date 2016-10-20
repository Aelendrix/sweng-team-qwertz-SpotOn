package ch.epfl.sweng.project;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.firebase.database.DatabaseReference;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TabActivity extends AppCompatActivity implements MyStoriesFragment.OnFragmentInteractionListener, CameraFragment.OnFragmentInteractionListener {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MapsActivity mMapFragment = new MapsActivity();
    // The path to the root of the stored pictures Data in the database
    private final String PATH_TO_PICTURE_DATA = "MediaDirectory";
    //DB
    private LocalDatabase mDB = new LocalDatabase(PATH_TO_PICTURE_DATA);
    //TimerTask
    private final int TIME_BETWEEN_EXEC = 10*1000; //1 minutes
    private Timer mTimer;
    //Location objects
    private LocationManager mLocationManager;
    private Location mLocation;
    //task that will be run every x Time.
    private TimerTask mTimerTask = new TimerTask() {

        @Override
        public void run() {
            //refresh the local database every minutes
            if(mLocation!=null) {
                mDB.refresh(mLocation);
                refreshMapMarkers(mDB);
            }
        }
    };

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

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("location","location Changed");
                refreshLocation();
                if(mLocation!=null){
                    if(mMapFragment!=null) {
                        mMapFragment.refreshMapLocation(mLocation);
                    }
                    //pictureFragment part
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        final int TIME_BETWEEN_LOCALISATION = 1*1000; //1 Second
        final int MIN_DISTANCE_CHANGE_UPDATE = 0; // 0 Meter
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_BETWEEN_LOCALISATION, MIN_DISTANCE_CHANGE_UPDATE, locationListener);
        }
        /*Catch exception because location acces always need to have the localisation permission
        * In our app if the permission is rejected, we can't access this activity anyway (ATM)
        */
        catch(SecurityException e) {
            e.printStackTrace();
        }
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

    /*
    This method uses the options menu when this activity is launched
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }


    //will refresh the mapactivity fragments in function of the localDatabase
    private void refreshMapMarkers(LocalDatabase DB){
        if(mMapFragment!=null){
            mMapFragment.displayDBMarkers(DB);
        }
    }

    //refresh the current location
    private void refreshLocation(){
        try {
            if (mLocationManager != null) {
                //check if gps is enable
                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //get the location according of the gps
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.d("location","new location set");
                }
            }
        }
        /*Catch exception because location access always need to have the localisation permission
        * In our app if the permission is rejected, we can't access this activity anyway (ATM)
        */
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyStoriesFragment(), "My Stories");
        adapter.addFragment(new CameraFragment(), "Camera");
        adapter.addFragment(mMapFragment, "Maps");
        viewPager.setAdapter(adapter);
    }
}
