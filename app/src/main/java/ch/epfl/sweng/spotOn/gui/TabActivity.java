package ch.epfl.sweng.spotOn.gui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;


public class TabActivity extends AppCompatActivity {


    private SeePicturesFragment mPicturesFragment = new SeePicturesFragment();
    private TakePictureFragment mCameraFragment = new TakePictureFragment();
    private MapFragment mMapFragment = new MapFragment();
    // The path to the root of the stored pictures Data in the database
    //TimerTask
    private final int TIME_BETWEEN_EXEC = 60*1000; //60 seconds
    Handler mHandler = new Handler();
    //
    private Runnable loopedRefresh = new Runnable() {
        @Override
        public void run() {
            Log.d("Loop","refresh the database");
            refreshDB();
            // Repeat this the same runnable code block again
            mHandler.postDelayed(loopedRefresh, TIME_BETWEEN_EXEC);
        }
    };
    //Location objects
    private LocationManager mLocationManager;
    private Location mLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
                LocalDatabase.setLocation(location);


            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        final int TIME_BETWEEN_LOCALISATION = 2*1000; //2 Second
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
    public void onBackPressed(){
    }

    public void dispatchTakePictureIntent(View view) {
        mCameraFragment.dispatchTakePictureIntent(view);
    }

    /**
     * Override method starting the repeating task every TIME_BETWEEN_EXEC seconds
     */
    @Override
    protected void onStart() {
        super.onStart();
        //start a looped runnable code
        mHandler.postDelayed(loopedRefresh,3*1000);

    }
    /**
     * Override method stopping the reapeting task
     */
    @Override
    protected void onStop(){
        super.onStop();
        //stop the timer
        mHandler.removeCallbacks(loopedRefresh);
    }

    public void rotatePicture(View view) {
        mCameraFragment.rotatePicture(view);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mPicturesFragment, getResources().getString(R.string.tab_aroundme));
        adapter.addFragment(mCameraFragment, getResources().getString(R.string.tab_camera));
        adapter.addFragment(mMapFragment, getResources().getString(R.string.tab_map));
        viewPager.setAdapter(adapter);
    }
    /**
    This method uses the options menu when this activity is launched
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.log_out:
                disconnectFacebook();
                return true;
            case R.id.action_about:
                Intent intent = new Intent(this, AboutPage.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void disconnectFacebook() {
        Profile profile = Profile.getCurrentProfile();
        if(profile != null){
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }


    /**
     * private class refreshing the local database using the firebase server
     * it'll update the mapFragment and display the photoObject on the map as markers
     */
    private void refreshDB() {
        if (mLocation != null) {
            LocalDatabase.refresh(mLocation,this);
        }

    }

    /**
     * public class following the call of refreshDB,
     * refreshing the map and the grid when the datas from firebase are downloaded
     */
    public void endRefreshDB(){
        if (mMapFragment != null) {
            mMapFragment.displayDBMarkers();
        }
        if (mPicturesFragment != null) {
            mPicturesFragment.refreshGrid();
        }
    }
    //refresh the local markers
    public void changeLocalMarkers(ArrayList<PhotoObject> photoList)
    {
        mMapFragment.displayPictureMarkers(photoList);
    }
    /**
     * Private classe refreshing the current location
     * and update the (mapFragment and pictureFragment) fragment's local variable of the location.
     */
    private void refreshLocation(){
        try {
            if (mLocationManager != null) {
                //check if gps is enable
                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //get the location according of the gps
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.d("location","new location set");
                    if(mLocation!=null){
                        if(mMapFragment!=null) {
                            mMapFragment.refreshMapLocation(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
                        }
                        if(mCameraFragment!=null) {
                            mCameraFragment.refreshLocation(mLocation);
                        }
                    }
                }
            }
        }
        /**
         * Catch exception because location access always need to have the localisation permission
        * In our app if the permission is rejected, we can't access this activity anyway (ATM)
        */
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }
}
