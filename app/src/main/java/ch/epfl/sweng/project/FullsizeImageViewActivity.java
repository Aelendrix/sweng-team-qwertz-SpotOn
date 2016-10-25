package ch.epfl.sweng.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class FullsizeImageViewActivity extends AppCompatActivity {

    public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.WANTED_IMAGE_PICTUREID";
    private final static Bitmap IMAGE_DEFAULT = //TODO
    private final static Bitmap IMAGE_FAILURE = //TODO

    private PhotoObject mDisplayedMedia = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullsize_image_view);

        ImageView viewToSet = (ImageView) findViewById(R.id.fullSizeImageView);
        // TODO - set a default image or thumbnail

        Intent displayImageIntent = getIntent();
        String wantedImagePictureId = displayImageIntent.getExtras().getString(WANTED_IMAGE_PICTUREID);

        Bitmap imageToDisplay = IMAGE_FAILURE;
        if(LocalDatabase.hasKey(wantedImagePictureId)){
            mDisplayedMedia = LocalDatabase.getKey(wantedImagePictureId);
            imageToDisplay = mDisplayedMedia.getFullSizeImage();
        }

        viewToSet.setImageBitmap(imageToDisplay);
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
