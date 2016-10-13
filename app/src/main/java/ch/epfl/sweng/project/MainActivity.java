/** Use of code that can be found here:
 * https://developers.facebook.com/docs/facebook-login/android/
 *
 */

package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


// Add this to the header of your file:
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

/**
 * Your app's main activity.
 */
public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "ch.epfl.sweng.project.MESSAGE";

    private LoginButton loginButton;

    public static int add(final int a, final int b) {
        return a + b;
    }


    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        // Useless in our case but will be useful for next activity launched
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        TextView mainText = (TextView) findViewById(R.id.mainTextView);
                        mainText.setText("success yesss");
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                TextView mainText = (TextView) findViewById(R.id.mainTextView);
                mainText.setText("success");

                goToMainMenu();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void goToMainMenu() {
        Intent intent = new Intent(this, MainMenu.class);
        String username = "mlb";
        intent.putExtra(EXTRA_MESSAGE, username);
        startActivity(intent);

    }

}