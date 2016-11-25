package ch.epfl.sweng.spotOn.gui;

import android.Manifest;
import android.app.Activity;
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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.location.Location;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.sql.Timestamp;

import ch.epfl.sweng.spotOn.BuildConfig;
import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.utils.ToastProvider;


/**
 * Activity that will allow the user to access the camera and take a picture to integrate
 * it in the app
 */
public class TakePictureFragment extends Fragment {

    private final DatabaseReference UserRef = DatabaseRef.getUsersDirectory();
    private final String USER_ID = User.getInstance().getUserId();
    private long mRemainingPhotos = User.getInstance().getRemainingPhotos();

    //id to access to the camera
    private static final int REQUEST_IMAGE_CAPTURE = 10;
    private static final int REQUEST_EDITION = 20;
    private ImageView mImageView;
    private Uri mImageToUploadUri;
    private PhotoObject mActualPhotoObject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.activity_picture, container, false);
        mImageView = (ImageView) view.findViewById(R.id.image_view);
        getRemainingPhotoInDay();
        return view;
    }


    /**
     * Method that checks if the app has the permission to use the camera
     * if not, it asks the permission to use it, else it calls the method invokeCamera()
     */
    public void dispatchTakePictureIntent(View view){
        /*SharedPreferences bb = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        mTextToDraw = bb.getString("TD", "");*/

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            invokeCamera();
        } else {
            String[] permissionRequested = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(getActivity(), permissionRequested, REQUEST_IMAGE_CAPTURE);
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

    /**
     * Uploads picture to our database/file server
     */
    public void sendPictureToServer(View view){
        if(mActualPhotoObject != null){
            if(!mActualPhotoObject.isStoredInServer()){

                if(mRemainingPhotos > 0 || USER_ID.equals("test")) {
                    --mRemainingPhotos;
                    UserRef.child(USER_ID).child("RemainingPhotos").setValue(mRemainingPhotos);
                    User.getInstance().setRemainingPhotos(mRemainingPhotos);
                    mActualPhotoObject.upload(true, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.getException()!=null){
                                ToastProvider.printOverCurrent("Internal error while uploading your post", Toast.LENGTH_LONG);
                            }else{
                                Log.d("TakePictureActivity","uploaded picture");
                                ToastProvider.printOverCurrent("Your pic is online !\nYou can post "+mRemainingPhotos+" more photos today!", Toast.LENGTH_LONG);
                            }
                        }
                    });
                    mActualPhotoObject.setSentToServerStatus(true);
                } else {
                    ToastProvider.printOverCurrent("You can't post anymore photos for today\n#FeelsBadMan", Toast.LENGTH_LONG);
                    Log.d("TakePictureFragment","User "+USER_ID+" can't post photo anymore");
                }

            } else {
                Toast.makeText(this.getActivity(), "This picture is already online", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this.getActivity(), "You need to take a picture in order to send it", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method called when clicking the "Edit" Button, it goes to the EditPicture activity
     * @param view
     */
    public void editPicture(View view){
        if(mActualPhotoObject != null){
            Intent editPictureIntent = new Intent(getContext(), EditPictureActivity.class);
            editPictureIntent.putExtra("bitmapToEdit", mImageToUploadUri.toString());
            startActivityForResult(editPictureIntent, REQUEST_EDITION);
        } else {
            Toast.makeText(this.getActivity(), "You need to take a picture in order to edit it", Toast.LENGTH_LONG).show();
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
            //mImageToUploadUri = PhotoUtils.createFileForBitmapAndGetUri("/SpotOn/TEMP_PICTURE.jpg", getContext())
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
    public static Bitmap getBitmap(Uri photoUri,Context context){
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

        if(!ConcreteLocationTracker.instanceExists()){
            throw new AssertionError("Location tracker should be started");
        }
        if(!ConcreteLocationTracker.getInstance().hasValidLocation()){
            // was checked for in the calling method
            throw new IllegalStateException("can't create new object without a valid location (should be tested before calling createPhotoObject");
        }
        Location currentLocation = ConcreteLocationTracker.getInstance().getLocation();
        PhotoObject picObject = new PhotoObject(imageBitmap, USER_ID, imageName, created, currentLocation.getLatitude(), currentLocation.getLongitude());

        return picObject;
    }

    /**
     * Method that will be called after the user took/edited a picture
     *
     * @param requestCode the request code of another activity
     * @param resultCode  the result of whether the user accept the captured/edited picture
     * @param data        the intent that was used to go to this activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            processResult(mImageToUploadUri);
        }
        if(requestCode == REQUEST_EDITION && resultCode == Activity.RESULT_OK) {
            processResult(Uri.parse(data.getExtras().getString("editedBitmap")));
        }
    }

    /**
     * Method that will fetch the bitmap image from the Uri in parameter, set the image view with
     * the fetched bitmap and create a photo object from it
     * @param imageToUploadUri the Uri of where is stored the picture
     */
    public void processResult(Uri imageToUploadUri){
        if(imageToUploadUri != null) {
            //Get our saved picture from the file in a bitmap image and display it on the image view
            Uri selectedImage = imageToUploadUri;
            getContext().getContentResolver().notifyChange(selectedImage, null);
            Bitmap HQPicture = getBitmap(imageToUploadUri, getContext());
            if(HQPicture != null){
                //Creates a mutable copy of the bitmap.
                /*Bitmap modifiedPicture = HQPicture.copy(Bitmap.Config.ARGB_8888, true);
                if(mTextToDraw != null) {
                    //Edits the bitmap in a canvas
                    Canvas canvas = new Canvas(modifiedPicture);
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    paint.setTextSize(50);
                    float x = 50;
                    float y = modifiedPicture.getHeight() - 200;
                    paint.setFakeBoldText(true);
                    canvas.drawText(mTextToDraw, x, y, paint);
                    //Removes string from the preferences so the next picture taken by the user doesn't always draw the same string
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.remove("TD");
                    edit.apply();
                }*/
                mImageView.setImageBitmap(HQPicture);
                //Create a PhotoObject instance of the picture and send it to the file server + database
                if(!ConcreteLocationTracker.instanceExists() || !ConcreteLocationTracker.getInstance().hasValidLocation()){
                    Toast.makeText(getContext(), "Can't create post without proper Location data", Toast.LENGTH_LONG);
                } else {
                    mActualPhotoObject = createPhotoObject(HQPicture);
                }
            } else {
                // Toast.makeText(getContext(),"Error while capturing Image: HQPicture null",Toast.LENGTH_LONG).show();
                ToastProvider.printOverCurrent("Internal error while creating your post : HQpicture null", Toast.LENGTH_SHORT);
            }
        }
        else {
            // Toast.makeText(getContext(),"Error while capturing Image: Uri null",Toast.LENGTH_LONG).show();
            ToastProvider.printOverCurrent("Internal error while creating your post : URI null", Toast.LENGTH_SHORT);
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
                "/SpotOn");
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


    private void getRemainingPhotoInDay(){

        UserRef.orderByChild("userId").equalTo(USER_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(USER_ID == null){
                        Log.e("TakePictureFragment","getRemainingPhotoInDay: USER_ID is null");
                    }
                    else {
                        if (dataSnapshot.child(USER_ID).child("RemainingPhotos").getValue() != null) {
                            mRemainingPhotos = ((long) dataSnapshot.child(USER_ID).child("RemainingPhotos").getValue());
                            User.getInstance().setRemainingPhotos(mRemainingPhotos);
                        }
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}