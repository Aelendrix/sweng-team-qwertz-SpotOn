package ch.epfl.sweng.spotOn.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import ch.epfl.sweng.spotOn.R;

/**
 * Created by Alexis Dewaele on 16/11/2016.
 * This activity receives text to draw on picture from user.
 */

public class DrawTextActivity extends AppCompatActivity{
    public static final String EXTRA_TEXT = "ch.epfl.sweng.TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_text);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.3));
    }

    public void sendTextToDraw(View view) {
        Intent intent = new Intent(this, TakePictureFragment.class);
        EditText inputText = (EditText) findViewById(R.id.textToDraw);
        intent.putExtra(EXTRA_TEXT, inputText.getText());
        super.onBackPressed();
    }
}
