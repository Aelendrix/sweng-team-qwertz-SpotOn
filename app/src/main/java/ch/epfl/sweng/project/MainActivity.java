package ch.epfl.sweng.project;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Your app's main activity.
 */
public final class MainActivity extends AppCompatActivity {




    public final static String EXTRA_MESSAGE = "ch.epfl.sweng.project.MESSAGE";

    public static int add(final int a, final int b) {
        return a + b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void signIn(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        EditText mainUsername = (EditText) findViewById(R.id.mainEmailEditText);
        String username = mainUsername.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, username);
        startActivity(intent);
    }




}