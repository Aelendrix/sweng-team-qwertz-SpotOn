package ch.epfl.sweng.spotOn.gui;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;

public class UserProfileActivity extends AppCompatActivity {

    private TextView mFirstNameTextView = null;
    private TextView mLastNameTextView = null;
    private TextView mKarmaTextView = null;
    private User mUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mUser = UserManager.getInstance().getUser();

        if( !mUser.isLoggedIn() || mUser.getUserId()==null){
            Log.e("UserProfileActivity", "UserId is null");
        }
        else {

            mFirstNameTextView = (TextView) findViewById(R.id.profileFirstNameTextView);
            mLastNameTextView = (TextView) findViewById(R.id.profileLastNameTextView);
            mKarmaTextView = (TextView) findViewById(R.id.profileKarmaTextView);

            Context context = getApplicationContext();
            String toastMessage = "Please wait a little bit while your info are updating";

            Toast toast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT);
            toast.show();

            mFirstNameTextView.setText(mFirstNameTextView.getText() + " " + mUser.getFirstName());
            mLastNameTextView.setText(mLastNameTextView.getText() + " " + mUser.getLastName());
            mKarmaTextView.setText(mKarmaTextView.getText() + " " + mUser.getKarma());
        }

        final Button button = (Button) findViewById(R.id.profileBackButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goBackToTabActivity();
            }
        });
    }


    private void goBackToTabActivity(){
        finish();
    }

}
