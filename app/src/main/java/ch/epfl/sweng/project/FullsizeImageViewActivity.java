package ch.epfl.sweng.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class FullsizeImageViewActivity extends Activity {

    public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.FullsizeImageViewActivity.WANTED_IMAGE_PICTUREID";
    private final static int RESOURCE_IMAGE_DOWNLOADING = R.drawable.image_downloading;
    private final static int RESOURCE_IMAGE_FAILURE =  R.drawable.image_failure;

    private PhotoObject mDisplayedMedia = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullsize_image_view);

        ImageView viewToSet = (ImageView) findViewById(R.id.fullSizeImageView);
        // TODO - set a default image or thumbnail

        Intent displayImageIntent = getIntent();
        String wantedImagePictureId = displayImageIntent.getExtras().getString(WANTED_IMAGE_PICTUREID);

        if(LocalDatabase.hasKey(wantedImagePictureId)){
            mDisplayedMedia = LocalDatabase.getPhoto(wantedImagePictureId);
            Bitmap imageToDisplay = mDisplayedMedia.getFullSizeImage();
            viewToSet.setImageBitmap(imageToDisplay);
        }else{
            viewToSet.setImageResource(RESOURCE_IMAGE_FAILURE);
        }
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
