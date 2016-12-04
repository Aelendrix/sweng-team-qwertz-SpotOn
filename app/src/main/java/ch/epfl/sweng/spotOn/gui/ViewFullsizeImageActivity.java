package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;


import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

public class ViewFullsizeImageActivity extends Activity {

    //public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID";

    private FullScreenImageAdapter mFullScreenImageAdapter;
    private String userID;
    private TextView mTextView;

    private ImageButton mUpvoteButton;
    private ImageButton mDownvoteButton;
    private Button mReportButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fullsize_image);
        userID = UserManager.getInstance().getUser().getUserId();
        mTextView = (TextView) findViewById(R.id.UpvoteTextView);
        mUpvoteButton = (ImageButton) findViewById(R.id.upvoteButton);
        mDownvoteButton = (ImageButton) findViewById(R.id.downvoteButton);
        mReportButton = (Button) findViewById(R.id.reportButton);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        mFullScreenImageAdapter = new FullScreenImageAdapter(this);
        viewPager.setAdapter(mFullScreenImageAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Picture position", " : " + position);
                updateCurrentMedia(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Intent displayImageIntent = getIntent();
        int position = displayImageIntent.getIntExtra("position", SeePicturesFragment.mDefaultItemPosition);
        viewPager.setCurrentItem(position);
        updateCurrentMedia(position);
    }

    private void updateCurrentMedia(int position) {
        mFullScreenImageAdapter.setCurrentMedia(position);
        mFullScreenImageAdapter.refreshVoteTextView(position);

        //Get the status of the user on the seen picture to color the buttons
        boolean upvoted = mFullScreenImageAdapter.alreadyUpvoted(userID);
        boolean downvoted = mFullScreenImageAdapter.alreadyDownvoted(userID);
        boolean reported = mFullScreenImageAdapter.alreadyReported(userID);

        if(upvoted){
            colorForUpvote();
        } else if(downvoted){
            colorForDownvote();
        } else {
            colorNone();
        }

        if(reported){
            mReportButton.setBackgroundResource(R.drawable.button_shape_report_clicked);
        } else {
            mReportButton.setBackgroundResource(R.drawable.button_shape_report);
        }
    }

    public void recordUpvote(View view) {
        if( !UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(User.NOT_LOGGED_in_MESSAGE, Toast.LENGTH_SHORT);
        }else {
            mFullScreenImageAdapter.recordUpvote(view);
            mUpvoteButton.setBackgroundResource(R.drawable.button_shape_upvote_clicked);
            mDownvoteButton.setBackgroundResource(R.drawable.button_shape_downvote);
        }
    }

    public void recordDownvote(View view) {
        if( !UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(User.NOT_LOGGED_in_MESSAGE, Toast.LENGTH_SHORT);
        }else {
            mFullScreenImageAdapter.recordDownvote(view);
            mUpvoteButton.setBackgroundResource(R.drawable.button_shape_upvote);
            mDownvoteButton.setBackgroundResource(R.drawable.button_shape_downvote_clicked);
        }
    }

    public void reportOffensivePicture(View view) {
        if( !UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(User.NOT_LOGGED_in_MESSAGE, Toast.LENGTH_SHORT);
        }else {
            mFullScreenImageAdapter.reportOffensivePicture(view);
        }
    }

    private void colorForUpvote(){
        mUpvoteButton.setBackgroundResource(R.drawable.button_shape_upvote_clicked);
        mDownvoteButton.setBackgroundResource(R.drawable.button_shape_downvote);
    }

    private void colorForDownvote(){
        mUpvoteButton.setBackgroundResource(R.drawable.button_shape_upvote);
        mDownvoteButton.setBackgroundResource(R.drawable.button_shape_downvote_clicked);
    }

    private void colorNone(){
        mUpvoteButton.setBackgroundResource(R.drawable.button_shape_upvote);
        mDownvoteButton.setBackgroundResource(R.drawable.button_shape_downvote);
    }


}
