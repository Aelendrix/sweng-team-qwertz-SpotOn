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
import ch.epfl.sweng.spotOn.localisation.LocalisationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;


public class TabActivity extends AppCompatActivity {


    public LocalisationTracker mLocationTracker;
    private SeePicturesFragment mPicturesFragment = new SeePicturesFragment();
    private TakePictureFragment mCameraFragment = new TakePictureFragment();
    private MapFragment mMapFragment = new MapFragment();
    // The path to the root of the stored pictures Data in the database
    //TimerTask
    private final int TIME_BETWEEN_EXEC = 60*1000; //60 seconds
    private boolean hasLocalisation = false;
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
    private Location mLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        //Set up the toolbar where the different tabs will be located
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        mLocationTracker = new LocalisationTracker(this.getApplicationContext()){
            @Override
            public void refreshTrackerLocation(){
                // Called when a new location is found by the network location provider.
                if(!hasLocalisation){
                    synchronized (this) {
                        mHandler.postDelayed(loopedRefresh, 3 * 1000);
                        hasLocalisation = true;
                    }
                }
                refreshLocation();
            }
        };
    }

    /*
    Disables the hardware back button of the phone
     */
    @Override
    public void onBackPressed(){
    }

    public void dispatchTakePictureIntent(View view) {
        mCameraFragment.dispatchTakePictureIntent(view);
    }

    public void storePictureOnInternalStorage(View view){
        mCameraFragment.storePictureOnInternalStorage(view);
    }

    public void sendPictureToServer(View view){
        mCameraFragment.sendPictureToServer(view);
    }

    /**
     * Override method starting the repeating task every TIME_BETWEEN_EXEC seconds
     */
    @Override
    protected void onStart() {
        super.onStart();
        //start a looped runnable code
        if(hasLocalisation) {
            mHandler.postDelayed(loopedRefresh, 10 * 1000);
        }

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

    /*
     * Rotates the picture by 90°
     */
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

    /*
     * Handles what action to take when the user clicks on a menu item in the options menu
     */
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
            case R.id.user_profile:
                Intent profileIntent = new Intent(this, UserProfileActivity.class);
                startActivity(profileIntent); // go to the User Profile Activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void disconnectFacebook() {
        Profile profile = Profile.getCurrentProfile();
        if(profile != null){
            LoginManager.getInstance().logOut();
            //go to the mainActivity in the activity stack
            finish();
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
        else{
            Log.d("Loop","DB not refreshed: no localisation discovered yet");
        }

    }

    /**
     * public class following the call of refreshDB,
     * refreshing the map and the grid when the datas from firebase are downloaded
     */
    public void endRefreshDB(){
        if (mMapFragment != null) {
            mMapFragment.addDBMarkers();
        }
        if (mPicturesFragment != null) {
            mPicturesFragment.refreshGrid();
        }
    }

    /**
     * Private class refreshing the current location
     * and update the (mapFragment and pictureFragment) fragment's local variable of the location.
     */
    private void refreshLocation(){
        mLocation = LocalDatabase.getLocation();
        if(mLocation!=null){
            if(mMapFragment!=null) {
                mMapFragment.refreshMapLocation(new LatLng(mLocation.getLatitude(),mLocation.getLongitude()));
            }
            if(mCameraFragment!=null) {
                mCameraFragment.refreshLocation(mLocation);
            }
            if(mPicturesFragment!=null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPicturesFragment.refreshGrid();
                    }
                });
            }
        }
    }
}
