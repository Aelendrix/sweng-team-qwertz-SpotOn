package ch.epfl.sweng.spotOn.gui;

import android.app.DialogFragment;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;


import ch.epfl.sweng.spotOn.R;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.user.UserManager;

import ch.epfl.sweng.spotOn.utils.ServicesChecker;
import ch.epfl.sweng.spotOn.utils.SingletonUtils;
import ch.epfl.sweng.spotOn.utils.ToastProvider;


public class TabActivity extends AppCompatActivity{


    private SeePicturesFragment mPicturesFragment = new SeePicturesFragment();
    private TakePictureFragment mCameraFragment = new TakePictureFragment();
    private MapFragment mMapFragment = new MapFragment();

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // if some singletons were destroyed, re-initialize them
        SingletonUtils.initializeSingletons(getApplicationContext());
        //We need to refresh the Local Database so if the user is logged in to hide the pictures he reported
        LocalDatabase.getInstance().refresh();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);


        //Set up the toolbar where the different tabs will be located
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onResume(){
        super.onResume();
        ToastProvider.update(this);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        unloadLocalDataSingleton();
    }

    /*
    Disables the hardware back button of the phone
     */
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @SuppressWarnings("UnusedParameters")
    public void dispatchTakePictureIntent(View view) {
        mCameraFragment.dispatchTakePictureIntent();
    }

    @SuppressWarnings("UnusedParameters")
    public void storePictureOnInternalStorage(View view) {
        mCameraFragment.storePictureOnInternalStorage();
    }

    @SuppressWarnings("UnusedParameters")
    public void sendPictureToServer(View view){
        mCameraFragment.sendPictureToServer();
    }

    @SuppressWarnings("UnusedParameters")
    public void editPicture(View view){
        mCameraFragment.editPicture();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(mPicturesFragment, getResources().getString(R.string.tab_aroundme));
        //this tab is useless if you are not logged-in
        if(UserManager.getInstance().isLogInThroughFacebook()) {
            adapter.addFragment(mCameraFragment, getResources().getString(R.string.tab_camera));
        }
        adapter.addFragment(mMapFragment, getResources().getString(R.string.tab_map));
        viewPager.setAdapter(adapter);
    }

    /* This method uses the options menu when this activity is launched     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(UserManager.getInstance().isLogInThroughFacebook()) {
            inflater.inflate(R.menu.options, menu);
        }
        else{
            inflater.inflate(R.menu.options_no_user, menu);
        }
        return true;
    }

    /* Handles what action to take when the user clicks on a menu item in the options menu     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //no need to break in this switch, because we return a boolean
        switch (item.getItemId()) {
            case R.id.log_out:
                showDialog();
                return true;

            case R.id.action_about:
                Intent intent = new Intent(this, AboutPage.class);
                startActivity(intent);
                return true;
            case R.id.user_profile:
                if( ! UserManager.getInstance().userIsLoggedIn() ){
                    ToastProvider.printOverCurrent(ServicesChecker.getInstance().provideLoginErrorMessage(), Toast.LENGTH_SHORT);
                    return false;
                }else {
                    Intent profileIntent = new Intent(this, UserProfileActivity.class);
                    startActivity(profileIntent); // go to the UserManager Profile Activity
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void unloadLocalDataSingleton(){
        //disable the service checker to remove the toast
        ServicesChecker.allowDisplayingToasts(false);
        //TODO: firebase.reset
        LocalDatabase.getInstance().clear();
    }


    @SuppressWarnings("UnusedParameters")
    public void onEmptyGridButtonClick(View v){
        //click on the rightmost tab every time
            mTabLayout.getTabAt(mTabLayout.getTabCount()-1).select();
    }

    @SuppressWarnings("UnusedParameters")
    public void onExtendOrderList(View v){
        RelativeLayout OrderListLayout = (RelativeLayout) findViewById(R.id.extended_list);
        ImageButton orderListButton = (ImageButton) findViewById(R.id.extend_list_button);
        orderListButton.setImageResource(android.R.color.transparent);
        if(OrderListLayout.getVisibility()==View.VISIBLE) {
            OrderListLayout.setVisibility(View.GONE);
            orderListButton.setImageResource(R.drawable.ic_format_list_numbered_black_32dp);
        }
        else{
            OrderListLayout.setVisibility(View.VISIBLE);
            orderListButton.setImageResource(R.drawable.ic_clear_black_32dp);


        }
    }

    @SuppressWarnings("UnusedParameters")
    public void onUpVoteOrderingClick(View v){
        ToastProvider.printOverCurrent("Ordered by most upvoted Picture",Toast.LENGTH_SHORT);
        refreshGrid(SeePicturesFragment.UPVOTE_ORDER);
        hideOrderMenu();
    }

    @SuppressWarnings("UnusedParameters")
    public void onOldestOrderingClick(View v){
        ToastProvider.printOverCurrent("Ordered by oldest Picture",Toast.LENGTH_SHORT);
        refreshGrid(SeePicturesFragment.OLDEST_ORDER);
        hideOrderMenu();
    }

    @SuppressWarnings("UnusedParameters")
    public void onNewestOrderingClick(View v){
        ToastProvider.printOverCurrent("Ordered by newest Picture",Toast.LENGTH_SHORT);
        refreshGrid(SeePicturesFragment.NEWEST_ORDER);
        hideOrderMenu();
    }

    @SuppressWarnings("UnusedParameters")
    public void onHottestOrderingClick(View v){
        ToastProvider.printOverCurrent("Ordered by hottest Picture",Toast.LENGTH_SHORT);
        refreshGrid(SeePicturesFragment.HOTTEST_ORDER);
        hideOrderMenu();
    }

    private void hideOrderMenu(){
        RelativeLayout OrderListLayout = (RelativeLayout) findViewById(R.id.extended_list);
        ImageButton orderListButton = (ImageButton) findViewById(R.id.extend_list_button);
        OrderListLayout.setVisibility(View.GONE);
        orderListButton.setImageResource(android.R.color.transparent);
        orderListButton.setImageResource(R.drawable.ic_format_list_numbered_black_32dp);

    }

    private void refreshGrid(int ordering){
        if(mPicturesFragment!=null){
            mPicturesFragment.refreshGrid(ordering);
        }
    }

    public void showDialog() {
        DialogFragment dialog = new FacebookLogOutDialog();
        dialog.show(getFragmentManager(), "FacebookLogOut");
    }

}
