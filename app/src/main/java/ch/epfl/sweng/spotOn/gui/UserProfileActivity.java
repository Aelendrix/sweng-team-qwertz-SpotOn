package ch.epfl.sweng.spotOn.gui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabaseListener;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.user.User;

public class UserProfileActivity extends AppCompatActivity implements LocalDatabaseListener {

    private TextView mFirstNameTextView = null;
    private TextView mLastNameTextView = null;
    private TextView mKarmaTextView = null;
    private ListView mPicturesListView = null;

    private User mUser = null;

    private static List<String> mPictureIdList = null;
    private static List<PhotoObject> mPhotoList = null;
    private static PictureVoteListAdapter mPictureVoteAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mUser = User.getInstance();
        mUser.getUserAttributesFromDB();

        if(mUser.getUserId() == null){
            throw new IllegalStateException("UserProfileActivity userId is null");
        }
        else {

            mFirstNameTextView = (TextView) findViewById(R.id.profileFirstNameTextView);
            mLastNameTextView = (TextView) findViewById(R.id.profileLastNameTextView);
            mKarmaTextView = (TextView) findViewById(R.id.profileKarmaTextView);

            mPicturesListView = (ListView) findViewById(R.id.profilePicturesListView);

            LocalDatabase.getInstance().addListener(this);
            refreshVoteAndPictureLists();

            Context context = getApplicationContext();
            String toastMessage = "Please wait a little bit while your info are updating";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, toastMessage, duration);
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


    @Override
    public void databaseUpdated() {
        refreshVoteAndPictureLists();
    }


    private void refreshVoteAndPictureLists(){
        if(mPictureIdList == null) {
            mPictureIdList = new ArrayList<>(mUser.getPhotosTaken().keySet());
        }
        else {
            mPictureIdList.removeAll(mPictureIdList);
            mPictureIdList.addAll(mUser.getPhotosTaken().keySet());
        }

        if(mPhotoList == null) {
            mPhotoList = new ArrayList<>();
        }
        else {
            mPhotoList.removeAll(mPhotoList);
        }

        for(int i=0; i<mPictureIdList.size(); i++){
            Log.d("pictureIdList", mPictureIdList.get(i));
            Log.d("photoListSize", Integer.toString(mPhotoList.size()));
            mPhotoList.add(LocalDatabase.getInstance().get(mPictureIdList.get(i)));
            Log.d("photoListSize", Integer.toString(mPhotoList.size()));
        }

        if(mPictureVoteAdapter == null) {
            mPictureVoteAdapter = new PictureVoteListAdapter(UserProfileActivity.this, mPhotoList);
        }
        else {
            mPictureVoteAdapter.updateList(mPhotoList);
        }
        mPicturesListView.setAdapter(mPictureVoteAdapter);
    }
}
