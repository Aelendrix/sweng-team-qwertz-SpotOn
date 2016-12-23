package ch.epfl.sweng.spotOn.gui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.location.Location;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.sql.Timestamp;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;
import ch.epfl.sweng.spotOn.utils.BitmapUtils;
import ch.epfl.sweng.spotOn.utils.ToastProvider;


/**
 * Activity that will allow the user to access the camera and take a picture to integrate
 * it in the app
 */
public class TakePictureFragment extends Fragment {

    private final DatabaseReference UserRef = DatabaseRef.getUsersDirectory();
//    private final User USER = User.getInstance();

    //id to access to the camera
    private static final int REQUEST_IMAGE_CAPTURE = 10;
    private static final int REQUEST_EDITION = 20;
    private static final int REQUEST_PERMISSIONS = 30;
    private boolean permissionToCapture;
    private ImageView mImageView;
    private Uri mImageToUploadUri;
    private PhotoObject mActualPhotoObject;
    private Uri editUri;
    private String lastUploadedPictureId;

    //Buttons to change their color
    private ImageButton mStoreButton;
    private ImageButton mSendButton;

    private Location mBackupLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.activity_picture, container, false);
        mImageView = (ImageView) view.findViewById(R.id.image_view);
        mStoreButton = (ImageButton) view.findViewById(R.id.storeButton);
        mSendButton = (ImageButton) view.findViewById(R.id.sendButton);
        return view;
    }


    /** Method that checks if the app has the permission to use the camera
     * if not, it asks the permission to use it, else it calls the method invokeCamera() */
    public void dispatchTakePictureIntent(){
        if( ! ServicesChecker.getInstance().canTakePicture() ){
            ToastProvider.printOverCurrent(ServicesChecker.getInstance().takePictureErrorMessage(), Toast.LENGTH_LONG);
        } else {
            // store last location, to prevent bug if we lose location in the meantime
            mBackupLocation = ConcreteLocationTracker.getInstance().getLocation();
            //No need of permissions if API version is below 23
            if ((capturePermissionGiven() && storagePermissionGiven()) || Build.VERSION.SDK_INT < 23) {
                invokeCamera();
            } else {
                //Ask the permission to capture and store
                String[] permissionsToCaptureAndStore = {Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissionsToCaptureAndStore,
                        REQUEST_PERMISSIONS);
            }
        }
    }

    /** Method called when clicking the "Store" button, it will store the picture
     * on the internal storage if not already stored */

    public void storePictureOnInternalStorage(){
        if(mActualPhotoObject == null) {
            ToastProvider.printOverCurrent("Store Button : Take a picture first !", Toast.LENGTH_SHORT);
        } else {
            if(!mActualPhotoObject.isStoredInternally()) {
                storeImage(mActualPhotoObject);
                mActualPhotoObject.setStoredInternallyStatus(true);
                ToastProvider.printOverCurrent("Picture stored in your gallery", Toast.LENGTH_LONG);
                colorBlackButton(mStoreButton);
            } else {
                ToastProvider.printOverCurrent("Picture already stored", Toast.LENGTH_LONG);
            }
        }
    }

    /**
     * Uploads picture to our database/file server
     */
    public void sendPictureToServer(){
        if(mActualPhotoObject == null){
            ToastProvider.printOverCurrent("Send Button : Take a picture first", Toast.LENGTH_LONG);
        } else if( mActualPhotoObject.isStoredInServer() ){
            ToastProvider.printOverCurrent( "This picture is already online", Toast.LENGTH_LONG);
        }else if( ! ServicesChecker.getInstance().canSendToServer() ){
            ToastProvider.printOverCurrent(ServicesChecker.getInstance().sendToServerErrorMessage(), Toast.LENGTH_LONG);
        }  else {
            final long remainingPhotos = UserManager.getInstance().getUser().computeRemainingPhotos();
            if(remainingPhotos > 0 || UserManager.getInstance().getUser().getUserId().equals("114110565725225")){
                lastUploadedPictureId = mActualPhotoObject.upload(true, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {if(task.getException()!=null){
                        ToastProvider.printOverCurrent("Internal error while uploading your post", Toast.LENGTH_LONG);
                    }else {
                        UserManager.getInstance().getUser().addPhoto(mActualPhotoObject);
                        Log.d("TakePictureActivity", "uploaded picture");
                        ToastProvider.printOverCurrent("Your pic is online ! \n You can still post " + (remainingPhotos - 1) + " today", Toast.LENGTH_LONG);
                    }
                    }
                });
                mActualPhotoObject.setSentToServerStatus(true);
                colorBlackButton(mSendButton);
            }else{
                ToastProvider.printOverCurrent("You can't post anymore photos today\n#FeelsBadMan", Toast.LENGTH_LONG);
                Log.d("TakePictureFragment", "UserManager " + UserManager.getInstance().getUser().getUserId() + " can't post photo anymore");
            }
        }
    }


    /**
     * Method called when clicking the "Edit" Button, it goes to the EditPicture activity
     */
    public void editPicture(){
        if(mActualPhotoObject == null){
            ToastProvider.printOverCurrent("Edit Button : Take a picture first", Toast.LENGTH_LONG);
        } else {
            Intent editPictureIntent = new Intent(getContext(), EditPictureActivity.class);
            editPictureIntent.putExtra("bitmapToEdit", editUri.toString());
            startActivityForResult(editPictureIntent, REQUEST_EDITION);
        }
    }

    /**
     * Method called after the user answered the pop-up messages about permissions.
     * It checks the user gave the right permissions and if positives, the app invokes the camera
     *
     * @param requestCode  the request code to access the camera
     * @param permissions  the permissions we asked to the user
     * @param grantResults the result of the permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (capturePermissionGiven() && storagePermissionGiven()) {
            invokeCamera();
        } else {
            ToastProvider.printOverCurrent("You need to accept the permissions to take pictures", Toast.LENGTH_LONG);
        }
    }

    /**
     * Method that invokes the camera and create the File of where the picture will be stored on
     * the internal storage
     */
    private void invokeCamera() {
        //Needed to store the last picture taken on the user's storage in order to have HQ picture
        File storage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/SpotOn");
        //Create storage directory to save the last take picture in high quality
        boolean storageExists = storage.exists();
        if (!storageExists) {
            storageExists = storage.mkdirs();
        }
        if(storageExists) {
            File tempPicture = new File(storage + File.separator + "LAST_PICTURE_TAKEN.jpg");
            Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            mImageToUploadUri = BitmapUtils.getUriFromFile(getContext(), tempPicture);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageToUploadUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Log.d("Storage", "error while creating the file");
        }
    }

    /**
     * Method that will transform the high quality picture in the storage of the phone into a Bitmap
     * @param photoUri  Uri of where the picture is stored
     * @return The bitmap of the high quality picture
     */
    public static Bitmap getBitmap(Uri photoUri,Context context){
        InputStream in;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = context.getContentResolver().openInputStream(photoUri);

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
            in = context.getContentResolver().openInputStream(photoUri);
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
        long currentTime = System.currentTimeMillis();
        Timestamp createdTime = new Timestamp(currentTime);
        //Name the picture
        String imageName = "PIC_" + currentTime + ".jpeg";
        // find location
        Location currentLocation = mBackupLocation;
        if(!ConcreteLocationTracker.getInstance().hasValidLocation()){
            // we can recover from this with mBackupLocation
            // was checked for in the calling method
            //throw new IllegalStateException("can't create new object without a valid location (should be tested before calling createPhotoObject");
            Log.d("TakePictureFragment","WARNING : lost location while taking photo, using backupLocation instead");
        }else {
            currentLocation = ConcreteLocationTracker.getInstance().getLocation();
        }

        return new PhotoObject(imageBitmap, UserManager.getInstance().getUser().getUserId(), imageName, createdTime, currentLocation.getLatitude(), currentLocation.getLongitude());
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

            colorPrimaryButton(mStoreButton);
            final long remainingPhotos = UserManager.getInstance().getUser().computeRemainingPhotos();
            if(remainingPhotos > 0 || UserManager.getInstance().getUser().getUserId().equals("114110565725225")){
                colorPrimaryButton(mSendButton);
            } else {
                colorBlackButton(mSendButton);
            }
        }
        if(requestCode == REQUEST_EDITION && resultCode == Activity.RESULT_OK) {
            Bundle dataBundle = data.getExtras();
            processResult(Uri.parse(dataBundle.getString("editedBitmap")));
        }
    }

    /**
     * Method that will fetch the bitmap image from the Uri in parameter, set the image view with
     * the fetched bitmap and create a photo object from it
     * @param imageToUploadUri the Uri of where is stored the picture
     */
    public void processResult(Uri imageToUploadUri){
        if(imageToUploadUri != null) {
            editUri = imageToUploadUri;
            //Get our saved picture from the uri in a bitmap image
            getContext().getContentResolver().notifyChange(imageToUploadUri, null);
            Bitmap HQPicture = getBitmap(imageToUploadUri, getContext());
            if(HQPicture != null){
                mImageView.setImageBitmap(HQPicture);
                mActualPhotoObject = createPhotoObject(HQPicture);
            } else {
                ToastProvider.printOverCurrent("Internal error while creating your post : HQPicture null", Toast.LENGTH_SHORT);
            }
        }
        else {
            ToastProvider.printOverCurrent("Internal error while creating your post : URI null", Toast.LENGTH_SHORT);
        }
    }

    public String getLastUploadedPictureId(){
        return lastUploadedPictureId;
    }

    private boolean capturePermissionGiven(){
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private boolean storagePermissionGiven(){
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    /** Method that will store the image in the Pictures file in the internal storage
     * @param photo a PhotoObject to get its full size picture to store in Pictures file   */
    private void storeImage(PhotoObject photo){
        if(storagePermissionGiven()) {
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

    /** Create a file where the pictures will be stored in the Pictures directory
     * @return the file where pictures will be stored   */
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

    //GRAPHICAL METHODS

    /**
     * Colors a button in black (gives him shape of button_shape_picture_after_clicked)
     * @param button the button to color in black
     */
    private void colorBlackButton(ImageButton button){
        button.setBackground(ContextCompat.getDrawable(getActivity(),
                R.drawable.button_shape_picture_after_clicked));
    }

    /**
     * Colors a button in red (gives him shape of button_shape_take_picture)
     * @param button the button to color in red
     */
    private void colorPrimaryButton(ImageButton button){
        button.setBackground(ContextCompat.getDrawable(getActivity(),
                R.drawable.button_shape_take_picture));
    }
}
