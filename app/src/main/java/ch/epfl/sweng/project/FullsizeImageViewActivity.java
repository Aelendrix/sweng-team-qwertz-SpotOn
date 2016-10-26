package ch.epfl.sweng.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class FullsizeImageViewActivity extends Activity {

    public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.FullsizeImageViewActivity.WANTED_IMAGE_PICTUREID";
    private final static int RESOURCE_IMAGE_DOWNLOADING = R.drawable.image_downloading;
    private final static int RESOURCE_IMAGE_FAILURE =  R.drawable.image_failure;

    private PhotoObject mDisplayedMedia = null;
    private ImageView mViewToSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullsize_image_view);

        mViewToSet = (ImageView) findViewById(R.id.fullSizeImageView);
        // TODO - set a default image or thumbnail

        Intent displayImageIntent = getIntent();
        String wantedImagePictureId = displayImageIntent.getExtras().getString(WANTED_IMAGE_PICTUREID);

        if(!LocalDatabase.hasKey(wantedImagePictureId)){
            mViewToSet.setImageResource(RESOURCE_IMAGE_FAILURE);
        }
        mDisplayedMedia = LocalDatabase.getPhoto(wantedImagePictureId);
        Bitmap imageToDisplay = null;
        if(mDisplayedMedia.hasFullSizeImage()){
            imageToDisplay = mDisplayedMedia.getFullSizeImage();
            mViewToSet.setImageBitmap(imageToDisplay);
        }else{
            // add a listener that will set the image when it is retrieved
            mDisplayedMedia.retrieveFullsizeImage(true, newImageViewSetterListener(), false, null);
        }
    }

    /** Factory method that returns a listener that sets the imageView with the result of its query
     */
    private OnSuccessListener newImageViewSetterListener(){
        return new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] imageAsByteArray) {
                Bitmap obtainedImage = BitmapFactory.decodeByteArray(imageAsByteArray, 0, imageAsByteArray.length);
                mViewToSet.setImageBitmap(obtainedImage);
            }
        };
    }
    /** Factory method that returns a listener that sets the imageView with the result of its query
     */
    private OnFailureListener failureImageSetterListene(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Exception", e.toString());
                mViewToSet.setImageResource(RESOURCE_IMAGE_FAILURE);
            }
        };
    }


/*    private void recordUpvote(){
        if(mDisplayedMedia!=null){

        }
    }

    private void recordDownvote(){
        if(mDisplayedMedia!=null){

        }
    }*/


}
