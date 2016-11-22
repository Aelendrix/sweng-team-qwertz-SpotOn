package ch.epfl.sweng.spotOn.gui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.epfl.sweng.spotOn.BuildConfig;
import ch.epfl.sweng.spotOn.R;

public class EditPictureActivity extends AppCompatActivity {

    //This Uri should not be modified: tells where is stored, on the phone, the picture the user is editing
    private Uri mURIofBitmap;

    private Bitmap mEditedBitmap;
    private ImageView mEditedImageView;
    public String mTextToDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);
        mEditedImageView = (ImageView) findViewById(R.id.image_view_edition);

        //Set the parameters of the activity (defined above) with the extras of the TakePicture activity
        Intent intent = getIntent();
        mURIofBitmap = Uri.parse(intent.getExtras().getString("bitmapToEdit"));
        mEditedBitmap = TakePictureFragment.getBitmap(mURIofBitmap, this);
        mEditedImageView.setImageBitmap(mEditedBitmap);
    }

    /**
     * Method that will be called when clicking on the Rotate button. It will rotate the image view
     * and rotate the bitmap image
     * @param view
     */
    public void rotatePicture(View view){
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(mEditedBitmap, 0, 0, mEditedBitmap.getWidth(),
                mEditedBitmap.getHeight(), rotationMatrix, true);
        mEditedBitmap = rotatedBitmap;
        mEditedImageView.setImageBitmap(rotatedBitmap);
    }

    /**
     * Method that will be called when clicking on the "Add Text" button
     * @param view
     */
    public void goToDrawTextActivity(View view){
        Intent intent = new Intent(this, DrawTextActivity.class);
        startActivity(intent);
    }

    /**
     * Method that will be called when clicking on the "Done" button to get back to the
     * TakePictureFragment activity and keeping the edited changes
     * @param view
     */
    public void confirmChanges(View view){
        Intent intent = new Intent();
        mURIofBitmap = getImageUri(mEditedBitmap);
        intent.putExtra("editedBitmap", mURIofBitmap.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void tryingRotation(View view){
        mEditedImageView.setImageBitmap(mEditedBitmap);
    }

    /**
     * Method that will write the edited bitmap on the internal storage and return the uri of
     * where is stored the image
     * @param bitmap the bitmap image we want to store
     * @return the uri of where is stored the image
     */
    public Uri getImageUri(Bitmap bitmap) {
        Uri resUri;
        File storageForEdition = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/SpotOn/EDITED_PICTURE.jpg");
        if(Build.VERSION.SDK_INT <= 23) {
            resUri = Uri.fromFile(storageForEdition);
            Log.d("URI ImageUpload", resUri.toString());
        } else {
            //For API >= 24 (was the cause of the crash)
            resUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider", storageForEdition);
            Log.d("URI ImageUpload", resUri.toString());
        }

        try {
            FileOutputStream pictureOutputFile = new FileOutputStream(storageForEdition);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, pictureOutputFile);
            pictureOutputFile.close();
            Log.d("Storage Permission", "accessed");

            //Allow the Pictures file to load directly after the image is stored
            MediaScannerConnection.scanFile(this, new String[]{storageForEdition.toString()}, null,
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
        return resUri;
    }
}
