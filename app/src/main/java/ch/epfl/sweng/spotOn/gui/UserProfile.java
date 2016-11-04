package ch.epfl.sweng.spotOn.gui;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserId;

public class UserProfile extends AppCompatActivity {

    TextView mFirstNameTextView = null;
    TextView mLastNameTextView = null;
    TextView mEmailAddressTextView = null;
    TextView mGenderTextView = null;
    TextView mBirthdayTextView = null;
    TextView mNbVotesTextView = null;
    TextView mNbPicturesTakenTextView = null;
    User mUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        UserId singletonUserId = UserId.getInstance();
        mUser = new User(singletonUserId.getUserId(), this);

        mFirstNameTextView = (TextView) findViewById(R.id.profileFirstNameTextView);
        mLastNameTextView = (TextView) findViewById(R.id.profileLastNameTextView);
        mEmailAddressTextView = (TextView) findViewById(R.id.profileEmailAddressTextView);
        mGenderTextView = (TextView) findViewById(R.id.profileGenderTextView);
        mBirthdayTextView = (TextView) findViewById(R.id.profileBirthdayTextView);
        mNbVotesTextView = (TextView) findViewById(R.id.profileNbVotesTextView);
        mNbPicturesTakenTextView = (TextView) findViewById(R.id.profileNbPicturesTakenTextView);
    }


    /* This method fills in the fields in the User Profile page */
    public void fillInFields(){
        mFirstNameTextView.setText(mFirstNameTextView.getText() + " " + mUser.getFirstName());
        mLastNameTextView.setText(mLastNameTextView.getText() + " " + mUser.getLastName());
    }

}
