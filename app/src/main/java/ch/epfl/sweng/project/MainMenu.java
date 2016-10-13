package ch.epfl.sweng.project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        TextView greetingMessage = (TextView) findViewById(R.id.greetingMessage);
        greetingMessage.setText("Hello !");

    }

}
