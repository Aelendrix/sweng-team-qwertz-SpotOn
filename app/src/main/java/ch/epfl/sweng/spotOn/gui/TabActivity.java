package ch.epfl.sweng.spotOn.gui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.util.ArrayList;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.media.PhotoObject;


public class TabActivity extends AppCompatActivity implements TabHost.OnTabChangeListener {


    private SeePicturesFragment mPicturesFragment = new SeePicturesFragment();
    private TakePictureFragment mCameraFragment = new TakePictureFragment();
    private MapFragment mMapFragment = new MapFragment();

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        //Set up the toolbar where the different tabs will be located
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
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


    /*
    Disables the hardware back button of the phone
     */
    @Override
    public void onBackPressed() {
    }

    public void dispatchTakePictureIntent(View view) {
        mCameraFragment.dispatchTakePictureIntent(view);
    }

    public void storePictureOnInternalStorage(View view) {
        mCameraFragment.storePictureOnInternalStorage(view);
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
     * This method uses the options menu when this activity is launched
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    /*
     * Handles what action to take when the user clicks on a menu item in the options menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        if (profile != null) {
            LoginManager.getInstance().logOut();
            //go to the mainActivity in the activity stack
            finish();
        }
    }


    @Override
    public void onTabChanged(String tabId) {
        // needed for my sprint 7 (quentin)
    }

    public void onEmptyGridButtonClick(View v){
        mTabLayout.getTabAt(2).select();
    }

    public void goToDrawTextActivity(View view) {
        mCameraFragment.goToDrawTextActivity(view);
    }

}
