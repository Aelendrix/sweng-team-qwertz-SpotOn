package ch.epfl.sweng.project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Activity that will allow the user to access the camera and take a picture to integrate
 * it in the app
 */
public class PictureActivity extends Fragment {

    //objet representing the phone localisation
    Location mPhoneLocation;
    private Toolbar mToolbar;

    //id to access to the camera
    private static final int REQUEST_IMAGE_CAPTURE = 10;

    //latitude and longitude, not always assigned
    private static double mLatitude;
    private static double mLongitude;

    private ImageView mPic;
    private LocationManager mLocationManager;
    private ArrayList<PhotoObject> mAllPictures = new ArrayList<PhotoObject>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_picture, container, false);

        mPic = (ImageView) view.findViewById(R.id.image_view);
        return view;
    }

    //function called when the locationListener see a location change
    public void refreshLocation(Location mPhoneLocation) {
                        mLatitude = mPhoneLocation.getLatitude();
                        mLongitude = mPhoneLocation.getLongitude();
    }

    /**
     * Method that checks if the app has the permission to use the camera
     * if not, it asks the permission to use it, else it calls the method invokeCamera()
     */

    public void dispatchTakePictureIntent(View view){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            invokeCamera();
        } else {
            String[] permissionRequested = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(getActivity(), permissionRequested, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void rotatePicture(View view) {
        mPic.setRotation(mPic.getRotation() + 90);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Storage", "Permission is granted");
                return true;
            } else {

                Log.v("Storage","Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Storage", "Permission is granted");
            return true;
        }
    }


    /**
     * Method called if the user never gave the permission. It checks the user's answer
     * and if positive, the app invokes the camera
     *
     * @param requestCode  the request code to access the camera
     * @param permissions  the permissions we asked to the user
     * @param grantResults the result of the permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                invokeCamera();
            } else {
                Toast.makeText(getContext(), getString(R.string.unable_to_invoke_camera), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Method that invokes the camera
     */
    public void invokeCamera() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Create a PhotoObject object from the picture taken
     * @param imageBitmap picture taken
     * @return a PhotoObject instance
     */
    private PhotoObject createPhotoObject(Bitmap imageBitmap){
        //Get the creation date for the timestamp
        java.util.Date date= new java.util.Date();
        Timestamp created = new Timestamp(date.getTime());
        //Name the picture
        long timestamp = System.currentTimeMillis();
        String imageName = "PIC_" + timestamp + ".jpeg";
        //TODO: Change Username and ID
        PhotoObject picObject = new PhotoObject(imageBitmap, "53", "Olivier", imageName, created, mLatitude, mLongitude, 100);
        mAllPictures.add(picObject);
        TabActivity tab= (TabActivity) getActivity();
        tab.changeLocalMarkers(mAllPictures);
        return picObject;
    }

    /**
     * Method that will put the captured photo in an image view
     * in the app if the user agreed so
     *
     * @param requestCode the request code to access the camera
     * @param resultCode  the result of whether the user kept the photo or canceled it
     * @param data        contains the image
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            //Display image on the activity
            Bundle extras = data.getExtras();
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            mPic.setImageBitmap(mImageBitmap);

            //Create a PhotoObject instance of the picture
            PhotoObject picObject = createPhotoObject(mImageBitmap);
            storeImage(picObject);

        }
    }

    /**
     * Method that will store the image in the Pictures file in the internal storage
     *
     * @param photo a PhotoObject to get its full size picture to store in Pictures file
     */

    private void storeImage(PhotoObject photo){
        //TEST sendToFileServer
        photo.sendToFileServer();

        if(isStoragePermissionGranted() == true) {
            File pictureFile = getOutputMediaFile(photo);

            if (pictureFile == null) {
                Log.d("Store Image", "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream pictureOutputFile = new FileOutputStream(pictureFile);
                photo.getFullSizeImage().compress(Bitmap.CompressFormat.PNG, 100, pictureOutputFile);
                pictureOutputFile.close();
                Log.d("Storage Permission", "accessed");

                //Allow the Pictures file to load directly after the image is stored
                MediaScannerConnection.scanFile(getContext(), new String[]{pictureFile.toString()}, null,
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
     *
     * @return the file where pictures will be stored
     */
    private File getOutputMediaFile(PhotoObject photo){
        File pictureDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/SpotOn/Pictures");
        Log.v("getOutputMediaFile", "accessed this one");

        //Create storage directory if it does not exist
        if (!pictureDirectory.exists()) {
            if (!pictureDirectory.mkdirs()) {
                return null;
            }
        }
        File pictureFile = new File(pictureDirectory.getPath() + File.separator + photo.getPhotoName());
        return pictureFile;
    }
}