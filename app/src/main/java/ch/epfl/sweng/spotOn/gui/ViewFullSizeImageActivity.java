package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

public class ViewFullSizeImageActivity extends Activity {

    //public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.ViewFullSizeImageActivity.WANTED_IMAGE_PICTUREID";

    private FullScreenImageAdapter mFullScreenImageAdapter;

    private String mUserID;
    private boolean mButtonsAreVisible;

    private ImageButton mUpvoteButton;
    private ImageButton mDownvoteButton;
    private Button mReportButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fullsize_image);
        
        mUpvoteButton = (ImageButton) findViewById(R.id.upvoteButton);
        mDownvoteButton = (ImageButton) findViewById(R.id.downvoteButton);
        mReportButton = (Button) findViewById(R.id.reportButton);
        mButtonsAreVisible = true;

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        //if user logged in he can make buttons appear or disappear by tapping on the screen
        if(UserManager.getInstance().userIsLoggedIn()) {
            mUserID = UserManager.getInstance().getUser().getUserId();
            //Needed to detect a tap on the viewPager
            final GestureDetector gestureDetector = new GestureDetector(this,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            // Removes or replaces the layout of the buttons on a singleTap of the user
                            if (mButtonsAreVisible) {
                                makeButtonsDisappear();
                                mButtonsAreVisible = false;
                            } else {
                                mUpvoteButton.setVisibility(View.VISIBLE);
                                mDownvoteButton.setVisibility(View.VISIBLE);
                                mReportButton.setVisibility(View.VISIBLE);
                                mButtonsAreVisible = true;
                            }
                            return true;
                        }
                    });

            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
        } else {
            //if the user is not logged in we delete the upvote/downvote/report buttons
            makeButtonsDisappear();
        }

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
        if(mUserID != null) {
            boolean upvoted = mFullScreenImageAdapter.alreadyUpvoted(mUserID);
            boolean downvoted = mFullScreenImageAdapter.alreadyDownvoted(mUserID);
            boolean reported = mFullScreenImageAdapter.alreadyReported(mUserID);

            if (upvoted) {
                colorForUpvote();
            } else if (downvoted) {
                colorForDownvote();
            } else {
                colorNone();
            }

            if (reported) {
                mReportButton.setBackgroundResource(R.drawable.button_shape_report_clicked);
            } else {
                mReportButton.setBackgroundResource(R.drawable.button_shape_report);
            }
        }
    }

    public void recordUpvote(View view) {
        if( ! UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(ServicesChecker.getInstance().provideLoginErrorMessage(), Toast.LENGTH_LONG);
        }else {
            mFullScreenImageAdapter.recordUpvote(view);
            //Change color of buttons only if the user is not th author of the picture
            if(!mUserID.equals(mFullScreenImageAdapter.getAuthorOfDisplayedPicture())) {
                if(!mFullScreenImageAdapter.alreadyUpvoted(mUserID))
                {
                    colorNone();
                }
                else {
                    colorForUpvote();
                }
            }
        }
    }

    public void recordDownvote(View view) {
        if( ! UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(ServicesChecker.getInstance().provideLoginErrorMessage(), Toast.LENGTH_LONG);
        }else {
            mFullScreenImageAdapter.recordDownvote(view);
            if(!mUserID.equals(mFullScreenImageAdapter.getAuthorOfDisplayedPicture())) {
                if(!mFullScreenImageAdapter.alreadyDownvoted(mUserID))
                {
                    colorNone();
                }
                else {
                    colorForDownvote();
                }
            }
        }
    }

    public void reportOffensivePicture(View view) {
        if( ! UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(ServicesChecker.getInstance().provideLoginErrorMessage(), Toast.LENGTH_LONG);
        }else {
            mFullScreenImageAdapter.reportOffensivePicture(view);
            //The color change of button is done in the above method reportOffensivePicture(view)
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

    private void makeButtonsDisappear(){
        mUpvoteButton.setVisibility(View.GONE);
        mDownvoteButton.setVisibility(View.GONE);
        mReportButton.setVisibility(View.GONE);
    }


}
