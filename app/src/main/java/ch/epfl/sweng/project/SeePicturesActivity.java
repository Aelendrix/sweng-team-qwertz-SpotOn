package ch.epfl.sweng.project;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.sweng.project.backgroudapplication.PhotoList;

public class SeePicturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_pictures);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PhotoList photos = PictureActivity.mSavedPhotos;

        if(! photos.isEmpty()) {
            //if there are some photos, show the first
            ImageView picture = (ImageView) findViewById(R.id.image_view);
            picture.setImageDrawable(photos.getPhotos().get(0).getPhoto());
        }
        //display some informations on the number of pictures stored in the PhotoList
        String textToDisplay = "There is/are "+photos.size()+" image(s) to display";
        TextView textView = (TextView) findViewById(R.id.pictureNumberInformation);
        textView.setText(textToDisplay);

    }

}
