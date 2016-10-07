package ch.epfl.sweng.project;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import static android.R.attr.data;

/**
 * Activity that will allow the user to access the camera and take a picture to integrate
 * it in the app
 */
public class PictureActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 10;
    private ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        Button click = (Button) findViewById(R.id.captureButton);
        pic = (ImageView) findViewById(R.id.image_view);

    }

    /**
     * Method that checks if the app has the permission to use the camera
     * if not, it asks the permission to use it, else it calls the method invokeCamera()
     * @param view
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
            pic.setImageBitmap(imageBitmap);
        }
    }

}
