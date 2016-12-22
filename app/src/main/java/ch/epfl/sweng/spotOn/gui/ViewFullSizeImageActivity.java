package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
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
                            } else if(UserManager.getInstance().isLogInThroughFacebook()) {
                                makeButtonsAppear();
                                mButtonsAreVisible = true;
                            }
                            return true;
                        }
                    });

            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.performClick();
                    return gestureDetector.onTouchEvent(event);
                }
            });
        } else {
            //if the user is not logged in we delete the upvote/downvote/report buttons
            makeButtonsDisappear();
        }

        mFullScreenImageAdapter = new FullScreenImageAdapter(this);
        viewPager.setAdapter(mFullScreenImageAdapter);

        Intent displayImageIntent = getIntent();
        int position = displayImageIntent.getIntExtra("position", SeePicturesFragment.mDefaultItemPosition);
        viewPager.setCurrentItem(position);
        updateCurrentMedia(position);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String wantedPicId = mFullScreenImageAdapter.getPicIdAtPosition(position);
                // If the picture is not in the local database anymore (author of the piture just erased it,
                // or the user is walking and the picture he is watching is not in range anymore...)
                if( ! LocalDatabase.getInstance().hasKey(wantedPicId)) {
                    Log.d("ViewFullSizeImage", "Swipe on a picture which was removed from database while browsing");
                    makeButtonsDisappear();
                    findViewById(R.id.UpvoteTextView).setVisibility(View.GONE);
                } else {
                    Log.d("PictureInLocal", "true");
                    updateCurrentMedia(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updateCurrentMedia(int position) {
        mFullScreenImageAdapter.setCurrentMedia(position);
        mFullScreenImageAdapter.refreshVoteTextView(position);

        //Get the status of the user on the seen picture to color the buttons
        if(mUserID != null) {
            boolean upvoted = mFullScreenImageAdapter.alreadyUpvoted(mUserID);
            boolean downvoted = mFullScreenImageAdapter.alreadyDownvoted(mUserID);

            if (upvoted) {
                colorForUpvote();
            } else if (downvoted) {
                colorForDownvote();
            } else {
                colorNone();
            }
        }
        if(UserManager.getInstance().isLogInThroughFacebook()) {
            makeButtonsAppear();
        }
    }

    public void recordUpvote(View view) {
        if( ! UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(ServicesChecker.getInstance().provideLoginErrorMessage(), Toast.LENGTH_LONG);
        }else {
            String picId = mFullScreenImageAdapter.getPicId();
            //If picture is in local database
            if(LocalDatabase.getInstance().hasKey(picId)) {
                mFullScreenImageAdapter.recordUpvote(view);
                //Change color of buttons only if the user is not the author of the picture
                if (!mUserID.equals(mFullScreenImageAdapter.getAuthorOfDisplayedPicture())) {
                    if (!mFullScreenImageAdapter.alreadyUpvoted(mUserID)) {
                        colorNone();
                    } else {
                        colorForUpvote();
                    }
                }
            } else {
                // You come in this loop only if the user is trying to upvote for a picture that is not
                // in his local database anymore (rare case)
                endActivity();
            }
        }
    }

    public void recordDownvote(View view) {
        if( ! UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(ServicesChecker.getInstance().provideLoginErrorMessage(), Toast.LENGTH_LONG);
        }else {
            String picId = mFullScreenImageAdapter.getPicId();
            //If picture is in local database
            if(LocalDatabase.getInstance().hasKey(picId)) {
                mFullScreenImageAdapter.recordDownvote(view);
                if (!mUserID.equals(mFullScreenImageAdapter.getAuthorOfDisplayedPicture())) {
                    if (!mFullScreenImageAdapter.alreadyDownvoted(mUserID)) {
                        colorNone();
                    } else {
                        colorForDownvote();
                    }
                }
            } else {
                // You come in this loop only if the user is trying to downvote for a picture that is not
                // in his local database anymore (rare case)
                endActivity();
            }
        }
    }
    public void reportOffensivePicture(View view) {
        reportDialog();
    }

    public void reportPicture() {
        if( ! UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(ServicesChecker.getInstance().provideLoginErrorMessage(), Toast.LENGTH_LONG);
        }else {
            String picId = mFullScreenImageAdapter.getPicId();
            //If picture is in local database
            if(LocalDatabase.getInstance().hasKey(picId)) {
                mFullScreenImageAdapter.reportOffensivePicture();
                if (!mUserID.equals(mFullScreenImageAdapter.getAuthorOfDisplayedPicture())) {
                    finish();
                }
            } else {
                // You come in this loop only if the user is trying to downvote for a picture that is not
                // in his local database anymore (rare case)
                endActivity();
            }
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

    private void makeButtonsAppear(){
        findViewById(R.id.UpvoteTextView).setVisibility(View.VISIBLE);
        mUpvoteButton.setVisibility(View.VISIBLE);
        mDownvoteButton.setVisibility(View.VISIBLE);
        mReportButton.setVisibility(View.VISIBLE);
    }

    /**
     * Method that finishes the activity (ViewFullSizeImage) to go back to the activity with the grid
     * of pictures and displays a toast message to the user
     */
    public void endActivity(){
        this.finish();
        String toastMessage = "This picture is not displayable anymore: the author may have deleted it or it is out of your range";
        ToastProvider.printOverCurrent(toastMessage, Toast.LENGTH_LONG);
    }

    public void reportDialog() {
        ReportPictureDialog dialog = new ReportPictureDialog();
        dialog.show(getFragmentManager(), "ReportPicture");
    }
}
