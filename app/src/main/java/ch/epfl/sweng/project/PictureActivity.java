package ch.epfl.sweng.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;

import ch.epfl.sweng.project.backgroudapplication.PassedTimestampFileDeletionService;
import ch.epfl.sweng.project.backgroudapplication.PhotoFile;
import ch.epfl.sweng.project.backgroudapplication.PhotoList;

import static java.lang.Long.max;

/**
 * Activity that will allow the user to access the camera and take a picture to integrate
 * it in the app
 */
public class PictureActivity extends AppCompatActivity {

    //objet representing the phone localisation
    Location mPhoneLocation;
    private Toolbar mToolbar;

    private static final int REQUEST_IMAGE_CAPTURE = 10;
    //latitude and longitude, not always assigned
    private static double mLatitude;
    private static double mLongitude;

    private ImageView mPic;
    private LocationManager mLocationManager;

    public static PhotoList mSavedPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    /*
        This method uses the options menu when this activity is launched
         */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
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

    /**
     * Method that checks if the app has the permission to use the camera
     * if not, it asks the permission to use it, else it calls the method invokeCamera()
     */
    public void dispatchTakePictureIntent(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            invokeCamera();
        } else {
            String[] permissionRequested = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this, permissionRequested, REQUEST_IMAGE_CAPTURE);
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
     * Method that invokes the camera
     */
    public void invokeCamera(){
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mPic.setImageBitmap(imageBitmap);
        }
    }

}
