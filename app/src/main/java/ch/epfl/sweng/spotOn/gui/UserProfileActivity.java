package ch.epfl.sweng.spotOn.gui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabaseListener;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;

public class UserProfileActivity extends AppCompatActivity implements LocalDatabaseListener {

    private ListView mPicturesListView = null;

    private User mUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mUser = UserManager.getInstance().getUser();

        if( !mUser.isLoggedIn() || mUser.getUserId()==null){
            Log.e("UserProfileActivity", "UserId is null");
            throw new IllegalStateException("UserProfileActivity userId is null");

        }
        else {

            TextView mFirstNameTextView = (TextView) findViewById(R.id.profileFirstNameTextView);
            TextView mLastNameTextView = (TextView) findViewById(R.id.profileLastNameTextView);
            TextView mKarmaTextView = (TextView) findViewById(R.id.profileKarmaTextView);

            mPicturesListView = (ListView) findViewById(R.id.profilePicturesListView);

            LocalDatabase.getInstance().addListener(this);
            refreshVoteAndPictureLists();
            
            //concatenate String is not advised in a setText, so we create the string before.
            String firstName = mFirstNameTextView.getText() + " " + mUser.getFirstName();
            String lastName = mLastNameTextView.getText() + " " + mUser.getLastName();
            String karmaValue = mKarmaTextView.getText() + " " + mUser.getKarma();
            mFirstNameTextView.setText(firstName);
            mLastNameTextView.setText(lastName);
            mKarmaTextView.setText(karmaValue);
        }
    }


    @Override
    public void databaseUpdated() {
        refreshVoteAndPictureLists();
    }

    private void refreshVoteAndPictureLists(){
        List<String> mPictureIdList = new ArrayList<>(mUser.retrieveUpdatedPhotosTaken().keySet());
        ArrayList<PhotoObject> mPhotoList = new ArrayList<>();

        for(int i=0; i<mPictureIdList.size(); i++){
            PhotoObject PO = LocalDatabase.getInstance().get(mPictureIdList.get(i));
            if(PO != null) {
                mPhotoList.add(PO);
            }
        }

        PictureVoteListAdapter mPictureVoteAdapter = new PictureVoteListAdapter(UserProfileActivity.this, mPhotoList);
        mPicturesListView.setAdapter(mPictureVoteAdapter);
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
