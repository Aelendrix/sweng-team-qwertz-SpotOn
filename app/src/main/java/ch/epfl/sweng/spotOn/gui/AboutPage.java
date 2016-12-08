package ch.epfl.sweng.spotOn.gui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import ch.epfl.sweng.spotOn.R;

/**
 *  Created by Alexis Dewaele
 *  Small popup in front of an activity, used to show text information
 */
public class AboutPage extends AppCompatActivity {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //popup is smaller than his parent
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.8), (int)(height*0.5));
    }
}
