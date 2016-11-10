package ch.epfl.sweng.spotOn.gui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserId;

public class UserProfileActivity extends AppCompatActivity {

    private TextView mFirstNameTextView = null;
    private TextView mLastNameTextView = null;
    private TextView mEmailAddressTextView = null;
    private TextView mGenderTextView = null;
    private TextView mBirthdayTextView = null;
    private TextView mNbVotesTextView = null;
    private TextView mNbPicturesTakenTextView = null;
    private TextView mKarmaTextView = null;
    private User mUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        UserId singletonUserId = UserId.getInstance();

        if(singletonUserId.getUserId() == null){
            Log.e("UserProfileActivity", "singletonUserId is null");
        }
        else {
            mUser = new User(singletonUserId.getUserId(), this);

            mFirstNameTextView = (TextView) findViewById(R.id.profileFirstNameTextView);
            mLastNameTextView = (TextView) findViewById(R.id.profileLastNameTextView);
            mEmailAddressTextView = (TextView) findViewById(R.id.profileEmailAddressTextView);
            mGenderTextView = (TextView) findViewById(R.id.profileGenderTextView);
            mBirthdayTextView = (TextView) findViewById(R.id.profileBirthdayTextView);
            mNbVotesTextView = (TextView) findViewById(R.id.profileNbVotesTextView);
            mNbPicturesTakenTextView = (TextView) findViewById(R.id.profileNbPicturesTakenTextView);
            mKarmaTextView = (TextView) findViewById(R.id.profileKarmaTextView);

            Context context = getApplicationContext();
            String toastMessage = "Please wait a little bit while your info are updating";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, toastMessage, duration);
            toast.show();
        }

        final Button button = (Button) findViewById(R.id.profileBackButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goBackToTabActivity();
            }
        });

    }


    /* This method fills in the fields in the User Profile page */
    public void fillInFields(){
        mFirstNameTextView.setText(mFirstNameTextView.getText() + " " + mUser.getFirstName());
        mLastNameTextView.setText(mLastNameTextView.getText() + " " + mUser.getLastName());
        mKarmaTextView.setText(mKarmaTextView.getText() + " " + mUser.getKarma());
    }


    private void goBackToTabActivity(){
        //start the TabActivity
        Intent intent = new Intent(this, TabActivity.class);
        startActivity(intent);
    }

}
