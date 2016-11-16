package ch.epfl.sweng.spotOn.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ch.epfl.sweng.spotOn.R;

/**
 * Created by Alexis Dewaele on 16/11/2016.
 */

public class DrawTextActivity extends AppCompatActivity{
    public static final String EXTRA_TEXT = "ch.epfl.sweng.TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_text);
    }
}
