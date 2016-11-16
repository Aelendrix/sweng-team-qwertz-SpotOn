package ch.epfl.sweng.spotOn.gui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
        EditText inputText = (EditText) findViewById(R.id.textToDraw);
        String text = inputText.getText().toString();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("TD", text);
        edit.apply();

        super.onBackPressed();
    }


}
