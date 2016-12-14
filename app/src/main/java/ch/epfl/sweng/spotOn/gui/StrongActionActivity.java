package ch.epfl.sweng.spotOn.gui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

public class StrongActionActivity extends AppCompatActivity {

    private String strongActionCode;
    private String pictureID;
    public static boolean report = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strong_action);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Intent intent = getIntent();
        strongActionCode = intent.getStringExtra(MainActivity.STRONG_ACTION_REQUEST);
        pictureID = intent.getStringExtra("currentPictureID");

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.3));
    }

    public void onDisapprovedStrongAction(View view) {
        onBackPressed();
    }

    public void onApprovedStrongAction(View view) {
        switch (strongActionCode) {
            case "Log out":
                if (!UserManager.getInstance().userIsLoggedIn()) {
                    // to provide a way to log back in - needs to be improved todo
                    finish();
                } else {
                    disconnectFacebook();
                    UserManager user = UserManager.getInstance();
                    user.destroyUser();
                    Intent mainIntent = new Intent(this, MainActivity.class);
                    startActivity(mainIntent);
                }
                break;
            case "Delete Picture":
                LocalDatabase.getInstance().removePhotoObject(pictureID);
                DatabaseRef.deletePhotoObjectFromDB(pictureID);
                StorageRef.deletePictureFromStorage(pictureID);
                UserManager.getInstance().getUser().removePhoto(pictureID);
                ToastProvider.printOverCurrent("Your picture has been deleted!", Toast.LENGTH_SHORT);
                finish();
                break;
            case "Report":
                report = true;
                break;
            default:
                ToastProvider.printIfNoCurrent("Not a valid strong action", Toast.LENGTH_SHORT);
                break;
        }
    }
    private void disconnectFacebook() {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            LoginManager.getInstance().logOut();
            //go to the mainActivity in the activity stack
            finish();
        }
    }

}
