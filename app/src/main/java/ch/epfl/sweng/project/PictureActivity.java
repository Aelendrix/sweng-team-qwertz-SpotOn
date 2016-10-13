package ch.epfl.sweng.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Activity that will allow the user to access the camera and take a picture to integrate
 * it in the app
 */
public class PictureActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 10;
    private ImageView mPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        mPic = (ImageView) findViewById(R.id.image_view);
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
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            mPic.setImageBitmap(mImageBitmap);
            storeImage(mImageBitmap);
        }
    }

    /**
     * Maethod that will store the image in the Pictures file in the internal storage
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
}
