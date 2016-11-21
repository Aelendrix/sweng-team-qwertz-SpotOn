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
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import ch.epfl.sweng.spotOn.R;

import ch.epfl.sweng.spotOn.user.User;

import ch.epfl.sweng.spotOn.utils.ServicesChecker;
import ch.epfl.sweng.spotOn.utils.ServicesCheckerListener;
import ch.epfl.sweng.spotOn.utils.ToastProvider;


public class TabActivity extends AppCompatActivity implements ServicesCheckerListener{


    private SeePicturesFragment mPicturesFragment = new SeePicturesFragment();
    private TakePictureFragment mCameraFragment = new TakePictureFragment();
    private MapFragment mMapFragment = new MapFragment();

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        ToastProvider.update(getApplicationContext());

        //Set up the toolbar where the different tabs will be located
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout){
            @Override
            public void onPageSelected(int pageNb) {
                checkAndDisplayServicesError();
            }
        });

    }


    /*
    Disables the hardware back button of the phone
     */
    @Override
    public void onBackPressed() {
        ToastProvider.printOverCurrent("THERE IS NO ESCAPE !", Toast.LENGTH_SHORT);
    }

    public void dispatchTakePictureIntent(View view) {
        mCameraFragment.dispatchTakePictureIntent(view);
    }

    public void storePictureOnInternalStorage(View view) {
        mCameraFragment.storePictureOnInternalStorage(view);
    }

    public void sendPictureToServer(View view){
        mCameraFragment.sendPictureToServer(view);
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
                User user = User.getInstance();
                user.destroy();
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

    public void onEmptyGridButtonClick(View v){
        mTabLayout.getTabAt(2).select();
    }

    public void goToDrawTextActivity(View view) {
        mCameraFragment.goToDrawTextActivity(view);
    }

// PRIVATE HELPERS
    /** displays the error message if need be    */
    public void checkAndDisplayServicesError(){
        if( ! ServicesChecker.getInstance().statusIsOk() ){
            String errorMessage = ServicesChecker.getInstance().provideErrorMessage();
            if( errorMessage.isEmpty() ){
                throw new IllegalStateException("incoherent state : error message can't be empty if status isn't good");
            }
            ToastProvider.printOverCurrent(errorMessage, Toast.LENGTH_LONG);
        }
    }


// LISTENER METHODS
    @Override
    public void servicesAvailabilityUpdated() {
        checkAndDisplayServicesError();
    }


}
