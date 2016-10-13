/** Use of code that can be found here:
 * https://developers.facebook.com/docs/facebook-login/android/
 *
 */

package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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

    private CallbackManager callbackManager;


    public static int add(final int a, final int b) {
        return a + b;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.mainLoginButton);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            // Process depending on the result of the authentication
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Once the user is connected
                goToMainMenu();
            }

            @Override
            public void onCancel() {
                // TODO: Add code
            }

            @Override
            public void onError(FacebookException exception) {
                // TODO: Add code
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void goToMainMenu() {
        // start a new activity
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);

    }
}