package ch.epfl.sweng.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FullsizeImageViewActivity extends AppCompatActivity {

    public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.WANTED_IMAGE_PICTUREID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullsize_image_view);

        Intent displayImageIntent = getIntent();
        String WantedImagePictureId = displayImageIntent.getExtras().getString(WANTED_IMAGE_PICTUREID);


    }


}
