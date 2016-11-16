package ch.epfl.sweng.spotOn.gui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
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

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;

import ch.epfl.sweng.spotOn.BuildConfig;
import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.user.UserId;


/**
 * Activity that will allow the user to access the camera and take a picture to integrate
 * it in the app
 */
public class TakePictureFragment extends Fragment {

    //objet representing the phone localisation
    Location mPhoneLocation;

    //id to access to the camera
    private static final int REQUEST_IMAGE_CAPTURE = 10;

    //latitude and longitude, not always assigned
    private static double mLatitude;
    private static double mLongitude;

    private ImageView mPic;
    private Uri mImageToUploadUri;
    private LocationManager mLocationManager;
    private PhotoObject mActualPhotoObject;
    public static String mTextToDraw;

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
        SharedPreferences bb = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        mTextToDraw = bb.getString("TD", "");
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            invokeCamera();
        } else {
            String[] permissionRequested = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(getActivity(), permissionRequested, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Method that will be called when clicking on the Rotate button. It will rotate the image view
     * and create a new PhotoObject from the rotatedPicture, but it will keep from the previous picture
     * the alreadyStored status and alreadySentToServer status in order for the user to avoid sending
     * the rotated picture if he already sent the non rotated picture before
     * @param view
     */
    public void rotatePicture(View view) {
        if(mActualPhotoObject != null){
            mPic.setRotation(mPic.getRotation() + 90);
            Bitmap original = mActualPhotoObject.getFullSizeImage();
            boolean alreadySentToServer = mActualPhotoObject.isStoredInServer();
            boolean alreadyStoredInternally = mActualPhotoObject.isStoredInternally();
            Matrix rotationMatrix = new Matrix();
            rotationMatrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(),
                    original.getHeight(), rotationMatrix, true);
            //Bitmap rotatedBitmap = ((BitmapDrawable)mPic.getDrawable()).getBitmap();
            mActualPhotoObject = createPhotoObject(rotatedBitmap);
            mActualPhotoObject.setSentToServerStatus(alreadySentToServer);
            mActualPhotoObject.setStoredInternallyStatus(alreadyStoredInternally);
        }
    }

    /**
     * Method called when clicking the "Store" button, it will store the picture
     * on the internal storage if not already stored
     */
    public void storePictureOnInternalStorage(View view){
        if(mActualPhotoObject != null) {
            if(!mActualPhotoObject.isStoredInternally()) {
                storeImage(mActualPhotoObject);
                mActualPhotoObject.setStoredInternallyStatus(true);
                Toast.makeText(this.getActivity(), "Picture stored in your internal storage", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getActivity(), "Picture already stored", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this.getActivity(), "You need to take a picture in order to store it.", Toast.LENGTH_LONG).show();
        }
    }

    public void sendPictureToServer(View view){
        if(mActualPhotoObject != null){
            if(!mActualPhotoObject.isStoredInServer()){
                mActualPhotoObject.upload(false, null); // no onCOmplete listener
                //TODO: Design something rather than displaying a message
                mActualPhotoObject.setSentToServerStatus(true);
                Toast.makeText(this.getActivity(), "Picture sent to server", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.getActivity(), "This picture is already online", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this.getActivity(), "You need to take a picture in order to send it", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks if the application has the storage permission
     * @return
     */
    private boolean isStoragePermissionGranted() {
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
    private void invokeCamera() {
        //Needed to store the last picture taken on the user's storage in order to have HQ picture
        if(isStoragePermissionGranted()) {
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            File temporalStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "/SpotOn/TEMP_PICTURE.jpg");
            if(Build.VERSION.SDK_INT <= 23) {
                mImageToUploadUri = Uri.fromFile(temporalStorage);
                Log.d("URI ImageUpload", mImageToUploadUri.toString());
            } else {
                //For API >= 24 (was the cause of the crash)
                mImageToUploadUri = FileProvider.getUriForFile(getContext(),
                        BuildConfig.APPLICATION_ID + ".provider", temporalStorage);
                Log.d("URI ImageUpload", mImageToUploadUri.toString());
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageToUploadUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Method that will transform the high quality picture in the storage of the phone into a Bitmap
     * @param photoUri  Uri of where the picture is stored
     * @return The bitmap of the high quality picture
     */
    private static Bitmap getBitmap(Uri photoUri,Context context){
        Uri uri = photoUri;
        InputStream in;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = context.getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Bitmap b = null;
            in = context.getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
                b.recycle();
                b = scaledBitmap;
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("Bitmap_Size", "bitmap width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("Getting_Bitmap", e.getMessage(), e);
            return null;
        }
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

        String userId = UserId.getInstance().getUserId();
        PhotoObject picObject = new PhotoObject(imageBitmap, userId, imageName, created, mLatitude, mLongitude);

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
            if(mImageToUploadUri != null) {
                //Get our saved picture from the file in a bitmap image and display it on the image view
                Uri selectedImage = mImageToUploadUri;
                getContext().getContentResolver().notifyChange(selectedImage, null);
                Bitmap HQPicture = getBitmap(mImageToUploadUri, getContext());
                if(HQPicture != null){
                    Canvas canvas = new Canvas(HQPicture);
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    paint.setTextSize(50);
                    float x = 100;
                    float y = HQPicture.getHeight() - 200;
                    paint.setFakeBoldText(true);
                    canvas.drawText(mTextToDraw, x, y, paint);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.remove("TD");
                    edit.apply();
                    mPic.setImageBitmap(HQPicture);
                    //Create a PhotoObject instance of the picture and send it to the file server + database
                    mActualPhotoObject = createPhotoObject(HQPicture);
                } else {
                    Toast.makeText(getContext(),"Error while capturing Image: HQPicture null",Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(),"Error while capturing Image: Uri null",Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * Method that will store the image in the Pictures file in the internal storage
     *
     * @param photo a PhotoObject to get its full size picture to store in Pictures file
     */

    private void storeImage(PhotoObject photo){
        if(isStoragePermissionGranted()) {
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
        pictureFile.setLastModified(photo.getCreatedDate().getTime());//we want last modified time to be created time of the photoObject
        return pictureFile;
    }
}