package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.utils.BitmapUtils;

/**
 * Created by Olivier and Alexis Dewaele
 * Activity to edit a BitMap picture (rotation)
 */
public class EditPictureActivity extends AppCompatActivity {

    private final static int REQUEST_TEXT_DRAWING = 30;

    private Uri mURIofBitmap;
    private Bitmap mEditedBitmap;
    private ImageView mEditedImageView;
    public String mTextToDraw;
    private Bitmap withoutTextBitmap;
    private boolean textIsDrawn = false;
    private int screenWidth;
    private int screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);
        mEditedImageView = (ImageView) findViewById(R.id.image_view_edition);

        //Set the parameters of the activity (defined above) with the extras of the TakePicture activity
        Intent intent = getIntent();
        mURIofBitmap = Uri.parse(intent.getExtras().getString("bitmapToEdit"));
        mEditedBitmap = TakePictureFragment.getBitmap(mURIofBitmap, this);
        withoutTextBitmap = mEditedBitmap;
        mEditedImageView.setImageBitmap(mEditedBitmap);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    /**
     * Listen to the touchscreen of the phone to draw the text on the finger position
     * @param event the touch event which get triggered
     * @return True if the event was handled (when there is a valid text to draw), false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float textPositionX = event.getX();
        float textPositionY = event.getY();

        if(mTextToDraw != null) {
            if(textIsDrawn) {
                mEditedBitmap = withoutTextBitmap;
            }
            drawText(textPositionX, textPositionY);
            return true;
        }

        return false;
    }

    /**
     * Method that will be called when clicking on the Rotate button. It will rotate the bitmap image
     * and set the image view with the rotated bitmap
     * @param view useless because called by a button
     */
    @SuppressWarnings("UnusedParameters")
    public void rotatePicture(View view){
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(withoutTextBitmap, 0, 0, withoutTextBitmap.getWidth(),
                withoutTextBitmap.getHeight(), rotationMatrix, true);
        mEditedBitmap = rotatedBitmap;
        withoutTextBitmap = rotatedBitmap;
        mEditedImageView.setImageBitmap(rotatedBitmap);
    }

    /**
     * Called when clicking on the "Add Text" button. Launch an activity popup to ask for the text
     * @param view useless because called by a button
     */
    @SuppressWarnings("UnusedParameters")
    public void goToDrawTextActivity(View view){
            Intent intent = new Intent(this, DrawTextActivity.class);
            startActivityForResult(intent, REQUEST_TEXT_DRAWING);
    }

    /**
     * Method that will be called when clicking on the "Done" button to get back to the
     * TakePictureFragment activity and keeping the edited changes
     * @param view useless because called by a button
     */
    @SuppressWarnings("UnusedParameters")
    public void confirmChanges(View view){
        Intent intent = new Intent();
        mURIofBitmap = storeAndGetImageUri(mEditedBitmap);
        //Give the Uri as a string in extras ! Just giving the Uri is not working
        intent.putExtra("editedBitmap", mURIofBitmap.toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Method that will be used when the user adds text to the picture through the DrawTextActivity
     *
     * @param requestCode the request code the finished activity gave
     * @param resultCode the result code the finished activity gave (OK or not)
     * @param data the intent from the finished activity to this one
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TEXT_DRAWING && resultCode == Activity.RESULT_OK){
            mTextToDraw = data.getStringExtra("textToDraw");
            Log.d("TextToDraw", mTextToDraw);
            float x = 50;
            float y = mEditedBitmap.getHeight() - 200;
            if(textIsDrawn) {
                mEditedBitmap = withoutTextBitmap;
            }
            textIsDrawn = drawText(x, y);
        }
    }

    /**
     * Method that will store the edited bitmap on the internal storage and return the uri of
     * where is stored the image
     * @param bitmap the bitmap image we want to store
     * @return the uri of where is stored the image
     */
    private Uri storeAndGetImageUri(Bitmap bitmap) {
        File storageForEdition = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/SpotOn/TEMP_PICTURE.jpg");
        Uri resUri = BitmapUtils.getUriFromFile(this, storageForEdition);

        //Store the picture at the directory defined above
        try {
            FileOutputStream pictureOutputFile = new FileOutputStream(storageForEdition);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, pictureOutputFile);
            pictureOutputFile.close();

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

    /**
     * Draw text on a BitMap in function of the given position
     * @param x the position in the X axis where we draw the text
     * @param y the position in the Y axis where we draw the text
     * @return true if the text is drawn, false otherwise
     */
    private boolean drawText(float x, float y) {
        Bitmap addTextBitmap = mEditedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int imgHeight = addTextBitmap.getHeight();
        int imgWidth = addTextBitmap.getWidth();
        if(mTextToDraw != null){
            //Edits the bitmap in a canvas
            Log.d("TextToDraw", "entered the loop");
            Canvas canvas = new Canvas(addTextBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setShadowLayer(10, 0, 0, Color.BLACK);
            int textSize = 50;
            paint.setTextSize(textSize);
            paint.setFakeBoldText(true);
            int offsetY = (int)(textSize*2.5);
            int offsetX = (int) paint.measureText(mTextToDraw)/2;
            //the parameter are scaled for the same position as the finger touch.
            canvas.drawText(mTextToDraw, (x-offsetX)*imgWidth/screenWidth, (y-offsetY)*imgHeight/screenHeight, paint);
            mEditedImageView.setImageBitmap(addTextBitmap);
            mEditedBitmap = addTextBitmap;
            return true;
        }
        return false;
    }
}
