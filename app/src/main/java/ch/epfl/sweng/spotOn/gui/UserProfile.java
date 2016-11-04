package ch.epfl.sweng.spotOn.gui;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserId;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        UserId singletonUserId = UserId.getInstance();
        User mUser = new User(singletonUserId.getUserId());


        TextView firstNameTextView = (TextView) findViewById(R.id.profileFirstNameTextView);
        firstNameTextView.setText(firstNameTextView.getText() + mUser.getFirstName());

    }



}
