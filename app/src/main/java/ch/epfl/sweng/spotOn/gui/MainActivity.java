package ch.epfl.sweng.spotOn.gui;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.Manifest;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.fileDeletionServices.ServerDeleteExpiredPhotoReceiver;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;
import ch.epfl.sweng.spotOn.utils.SingletonUtils;
import ch.epfl.sweng.spotOn.utils.ToastProvider;


/**
 * Your app's main activity.
 */
public final class MainActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private Profile mFbProfile;
    private ProfileTracker mFbProfileTracker;

    private final long TIME_BETWEEN_TWO_ALARM = 60 * 60 *1000;//one hour for now

    private final int REQUEST_FINE_LOCALISATION = 9;

    public final static String STRONG_ACTION_REQUEST = "ch.epfl.sweng.spotOn.gui.STRONG_ACTION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SingletonUtils.initializeSingletons(getApplicationContext());
        ToastProvider.update(this);
        ServicesChecker.allowDisplayingToasts(false);


        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        /*Create an alarm which will go off for sending queries to the Firebase server
         * to check the expiration time of the files.        */
        AlarmManager serverDataDeletionAlarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent serverDataDeletionIntent = new Intent(this, ServerDeleteExpiredPhotoReceiver.class);
        PendingIntent serverDataDeletionPendingIntent = PendingIntent.getBroadcast(this, 0, serverDataDeletionIntent, 0);
        serverDataDeletionAlarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), TIME_BETWEEN_TWO_ALARM, serverDataDeletionPendingIntent);


        setContentView(R.layout.activity_main);

        mCallbackManager = CallbackManager.Factory.create();

        // get the mainLoginButton (facebook login button)
        LoginButton mainLoginButton = (LoginButton) findViewById(R.id.mainLoginButton);
        mainLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            // Process depending on the result of the authentication
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Once the user is connected
                getFbProfile();
            }

            @Override
            public void onCancel() {
                // Display a welcome message when user authenticates
                Context context = getApplicationContext();
                String toastMessage = "The authentication has been cancelled";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, toastMessage, duration);
                toast.show();
            }

            @Override
            public void onError(FacebookException exception) {
                //Log the exception raised by Facebook
                Log.e("FacebookException", exception.getMessage());
            }
        });

        // Test if a user is already logged on when creating the MainActivity
        if(AccessToken.getCurrentAccessToken()!= null) {
            getFbProfile();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onResume(){
        super.onResume();
        ToastProvider.update(this);
    }


    /*@Override
    protected void onStart() {
        super.onStart();
        //check every time the MainActivity is started if we have the permission: ACCESS_FINE_LOCATION
        // and throw the user input in onRequestPermissionsResult
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_DENIED ){
            Log.d("MainActivity","No GPS Permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCALISATION);
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @SuppressWarnings("UnusedParameters")
    public void goToTabActivityNotLoggedIn(View view){
        goToTabActivity(false);
    }

    public void goToTabActivity(boolean loggedIn) {
        if( !LocalDatabase.instanceExists() || !ConcreteLocationTracker.instanceExists() || !UserManager.instanceExists()){
            throw new IllegalStateException("All required singletons need to be initialized before leaving the mainActivity");
        }
        if(loggedIn) {
            UserManager.getInstance().setUserFromFacebook(mFbProfile.getFirstName(), mFbProfile.getLastName(), mFbProfile.getId());
        }else{
            UserManager.getInstance().setEmptyUser();
        }

        // We only check here the location permission and not at the opening of the app because, it
        // caused a bug: if the phone went in stand-by mode before accepting/refusing the permission
        // the app wouldn't open anymore and this toast would be displayed: "This app needs this
        // permission to be open"
        if(locationPermissionGiven()) {
            // allow toasts to display error rmessaes
            ServicesChecker.allowDisplayingToasts(true);
            //start the TabActivity
            Intent intent = new Intent(this, TabActivity.class);
            startActivity(intent);
        } else {
            askForLocationPermission();
        }
    }


    /* Method to get the Facebook profile of the user */
    public void getFbProfile(){
        //get current Facebook profile
        if (Profile.getCurrentProfile() == null) {
            mFbProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                    //update the variable of the Facebook profile
                    mFbProfile = newProfile;

                    //login done so call to goToTabActivity
                    goToTabActivity(true);

                    mFbProfileTracker.stopTracking();
                }
            };
        } else {
            //get the current profile if profile not null
            mFbProfile = Profile.getCurrentProfile();

            //login done so call to goToTabActivity
            goToTabActivity(true);
        }
    }


    /**
     * Read the result of the location permission request, stay in the main activity if the user
     * refuses the permission or go to the TabActivity otherwise
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(locationPermissionGiven()){
            //start the tab activity
            Intent intent = new Intent(this, TabActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.gps_not_permitted), Toast.LENGTH_LONG).show();
            //TODO: find a way to trigger the permission before the user logs in
            if(UserManager.getInstance().userIsLoggedIn()){
                //Unlog user from facebook -> not annoying if you have facebook on your phone, you'll
                // log again instantly by pressing the login button
                LoginManager.getInstance().logOut();
                UserManager user = UserManager.getInstance();
                user.destroyUser();
            }
        }
    }

    private void askForLocationPermission(){
        String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, locationPermission, REQUEST_FINE_LOCALISATION);
    }

    private boolean locationPermissionGiven(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }
}