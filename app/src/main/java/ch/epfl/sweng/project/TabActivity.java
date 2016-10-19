package ch.epfl.sweng.project;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.epfl.sweng.project.backgroudapplication.PassedTimestampFileDeletionService;
import ch.epfl.sweng.project.backgroudapplication.PhotoFile;
import ch.epfl.sweng.project.backgroudapplication.PhotoList;

import com.google.firebase.database.DatabaseReference;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class TabActivity extends AppCompatActivity implements MyStoriesFragment.OnFragmentInteractionListener, CameraFragment.OnCameraSelectedListener, StoriesAroundMeFragment.OnFragmentInteractionListener {

    private static final int REQUEST_IMAGE_CAPTURE = 10;
    Location mPhoneLocation;
    //latitude and longitude, not always assigned
    private static double mLatitude = 0.0;
    private static double mLongitude = 0.0;

    private ImageView mPic;
    private LocationManager mLocationManager;

    public static PhotoList mSavedPhotos;


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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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



        mPic = (ImageView) findViewById(R.id.image_view);

        mSavedPhotos = new PhotoList();

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Call refreshLocation when a new location is found by the network location provider.
                Log.d("location","location Changed");
                refreshLocation();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        final int TIME_BETWEEN_LOCALISATION = 60 * 1000; //1 Minutes
        final int MIN_DISTANCE_CHANGE_UPDATE = 10; // 1 Meter
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

    public void onCameraSelected(Uri uri){
        Intent intent = new Intent(this, PictureActivity.class);
        startActivity(intent);
    }

    //function called when the locationListener see a location change
    private void refreshLocation() {
        try {
            if (mLocationManager != null) {
                //check if gps is enable
                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //get the location according of the gps
                    mPhoneLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (mPhoneLocation != null) {
                        mLatitude = mPhoneLocation.getLatitude();
                        mLongitude = mPhoneLocation.getLongitude();
                        //TODO: How to handle if the gps is slow or not working and we take a photo now?
                    }
                }
            }

        }
        /*Catch exception because location acces always need to have the localisation permission
        * In our app if the permission is rejected, we can't access this activity anyway (ATM)
        */
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    public void dispatchTakePictureIntent(View view){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            invokeCamera();
        } else {
            String[] permissionRequested = {android.Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissionRequested, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void invokeCamera(){
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    public void rotatePicture(View view){
        mPic.setRotation(mPic.getRotation() + 90);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Storage","Permission is granted");
                return true;
            } else {

                Log.v("Storage","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Storage","Permission is granted");
            return true;
        }
    }

    /**
     * Saves the last picture that has been took in a PhotoList and lauch a service that
     * deletes to old photos
     * @param view
     */

    public void savePicture(View view){
        EditText secondsToKeep = (EditText) findViewById(R.id.askHowMuchTime);

        //Control if there is an input, else put some default value
        long millisecondsToKeep = 5000;
        String secondsToKeepString = secondsToKeep.getText().toString();
        if(! secondsToKeepString.equals("")) {
            millisecondsToKeep = Long.parseLong(secondsToKeepString) * 1000;
        }
        //create a new PhotoFile and add it to the list, and then start the deleting service
        PhotoFile photo = new PhotoFile( mPic.getDrawable(),new Timestamp(System.currentTimeMillis()), millisecondsToKeep);
        mSavedPhotos.addPhoto(photo);
        Intent service = new Intent(this, PassedTimestampFileDeletionService.class);
        startService(service);
    }

    /**
     * Launch the activity which show the pictures that have been saved
     * @param view
     */
    public void goToSeePicturesActivity(View view){
        Intent intent = new Intent(this, SeePicturesActivity.class);
        startActivity(intent);
    }



    /**
     * Method called if the user never gave the permission. It checks the user's answer
     * and if positive, the app invokes the camera
     * @param requestCode the request code to access the camera
     * @param permissions the permissions we asked to the user
     * @param grantResults the result of the permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                invokeCamera();
            } else {
                Toast.makeText(this, getString(R.string.unable_to_invoke_camera), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Method that will put the captured photo in an image view
     * in the app if the user agreed so
     * @param requestCode the request code to access the camera
     * @param resultCode the result of whether the user kept the photo or canceled it
     * @param data contains the image
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Display image on the activity
            Bundle extras = data.getExtras();
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            mPic.setImageBitmap(mImageBitmap);
            //Store image
            storeImage(mImageBitmap);
            //Get the coordinates where the picture was taken and display them as toast message
            refreshLocation();
            String displayLocation = "Longitude is " + mLongitude + " and latitude is " + mLatitude;
            Toast.makeText(this, displayLocation, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method that will store the image in the Pictures file in the internal storage
     *
     * @param picture the bitmap picture to store in Pictures file
     */
    private void storeImage(Bitmap picture){
        if(isStoragePermissionGranted() == true) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d("Store Image", "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream pictureOutputFile = new FileOutputStream(pictureFile);
                picture.compress(Bitmap.CompressFormat.PNG, 100, pictureOutputFile);
                pictureOutputFile.close();
                Log.d("Storage Permission", "accessed");

                //Allow the Pictures file to load directly after the image is stored
                MediaScannerConnection.scanFile(this, new String[]{pictureFile.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
            } catch (FileNotFoundException e) {
                Log.d("Store Image", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("Store Image", "File not closed: " + e.getMessage());
            }
        } else {
            Log.d("Storage Permission", "not granted");
        }
    }
    /**
     * Create a file where the pictures will be stored in the Pictures directory
     * @return the file where pictures will be stored
     */
    private File getOutputMediaFile(){
        File pictureDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/SpotOn/Pictures");
        Log.v("getOutputMediaFile", "accessed this one");

        //Create storage directory if it does not exist
        if(!pictureDirectory.exists()){
            if(! pictureDirectory.mkdirs()){
                return null;
            }
        }
        //Name the picture
        String timestamp = new SimpleDateFormat("ddMMyyyy__HHmmss").format(new Date());
        String imageName = "PIC_"+ timestamp + ".jpeg";
        File pictureFile = new File(pictureDirectory.getPath() + File.separator + imageName);
        return pictureFile;
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
        adapter.addFragment(new MapsActivity(), "Stories around me");
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
