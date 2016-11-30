package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

/**
 * Created by Alexis Dewaele on 08/11/2016.
 */

public class FullScreenImageAdapter extends PagerAdapter {
    private Activity mActivity;

    private ImageAdapter mRefToImageAdapter;

    private ImageView mViewToSet;
    private int voteSum=0;
    private TextView mTextView;
    private ImageButton mUpvoteButton;
    private ImageButton mDownvoteButton;
    private Button mReportButton;
    private PhotoObject mDisplayedMedia;

    private final static int RESOURCE_IMAGE_DOWNLOADING = R.drawable.image_downloading;
    private final static int RESOURCE_IMAGE_FAILURE =  R.drawable.image_failure;



    public FullScreenImageAdapter(Activity activity) {
        mActivity = activity;
        mRefToImageAdapter = SeePicturesFragment.getImageAdapter();
    }

    @Override
    public int getCount() {
        return mRefToImageAdapter.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d("instantiateItem", "called1");
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false);
        mViewToSet = (ImageView) viewLayout.findViewById(R.id.fullSizeImageView);
        mViewToSet.setImageResource(RESOURCE_IMAGE_DOWNLOADING);

        if(position >= mRefToImageAdapter.size()){
            throw new ArrayIndexOutOfBoundsException();
        }

        String wantedPicId = mRefToImageAdapter.getIdAtPosition(position);
        if(!LocalDatabase.getInstance().hasKey(wantedPicId)){
            throw new NoSuchElementException("Localdatabase does not contain wanted picture : "+wantedPicId);
        }
        mDisplayedMedia = LocalDatabase.getInstance().get(wantedPicId);

        if (mDisplayedMedia.hasFullSizeImage()) {
            Bitmap imageToDisplay = mDisplayedMedia.getFullSizeImage();
            mViewToSet.setImageBitmap(imageToDisplay);
        } else {
            // want these final variable, because the fields of the class may change if we swipe
            final ImageView currentView = mViewToSet;
            final String currentPicId = new String(wantedPicId);
            mDisplayedMedia.retrieveFullsizeImage(true, new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> retrieveFullSizePicTask) {
                    if(retrieveFullSizePicTask.getException()!=null){
                        currentView.setImageResource(RESOURCE_IMAGE_FAILURE);
                        // maybe it's better if we recover from this, and use only a log. Tell me in the Pull Request Comments (also, I left it as is to proove it passes tests
                        throw new Error("FullScreenImageAdapter : Retrieving fullSizePicture with pictureid : \n"+currentPicId+"failed due to :\n "+retrieveFullSizePicTask.getException());
                        //Log.d("FullScreenImageAdapter","ERROR : couldn't get fullSizeImage for picture "+currentPicId);
                    }else{
                        Bitmap obtainedImage = BitmapFactory.decodeByteArray(retrieveFullSizePicTask.getResult(), 0, retrieveFullSizePicTask.getResult().length);
                        currentView.setImageBitmap(obtainedImage);
                    }
                }
            });
        }
        //upvotes
        mTextView = (TextView) viewLayout.findViewById(R.id.UpvoteTextView);
        voteSum = mDisplayedMedia.getUpvotes()-mDisplayedMedia.getDownvotes();
        refreshVoteTextView(Integer.toString(voteSum));

        View viewFullSize = inflater.inflate(R.layout.activity_view_fullsize_image, container, false);
        mUpvoteButton = (ImageButton) viewFullSize.findViewById(R.id.upvoteButton);
        mDownvoteButton = (ImageButton) viewFullSize.findViewById(R.id.downvoteButton);
        mReportButton = (Button) viewFullSize.findViewById(R.id.reportButton);
        String userID = User.getInstance().getUserId();
        colorButtons(userID);

        container.addView(viewLayout);
        container.addView(viewFullSize);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    public void refreshVoteTextView(String s){
        mTextView.setText(s);
    }

    public void recordUpvote(View view){
        vote(1);
    }

    public void recordDownvote(View view){
        vote(-1);
    }


    private void vote(int vote){
        if(mDisplayedMedia==null) {
            throw new NullPointerException("FullScreenImageAdapter : trying to vote on a null media");
        }else{
            String userId = User.getInstance().getUserId();
            //fake vote method to have more responsive interface
            if(vote==1&&!mDisplayedMedia.getAuthorId().equals(userId)&&!alreadyUpvoted(userId)){
                voteSum++;
                colorIfUpvote();
                if(alreadyDownvoted(userId)){
                    voteSum++;
                }
            }
            if(vote==-1&&!mDisplayedMedia.getAuthorId().equals(userId)&&!alreadyDownvoted(userId)){
                voteSum--;
                colorIfDownvote();
                if(alreadyUpvoted(userId)){
                    voteSum--;
                }
            }
            refreshVoteTextView(Integer.toString(voteSum));

            String toastMessage = mDisplayedMedia.processVote(vote, userId);
            ToastProvider.printOverCurrent(toastMessage,ToastProvider.SHORT);

        }
    }


    public void reportOffensivePicture(View view){
        if(mDisplayedMedia == null) {
            Log.e("FullScreenImageAdapter","reportOffensivePicture mDisplayedMedia is null");
        }else{
            String userId = User.getInstance().getUserId();
            //Change color of report button depending if the user reports or unreports the picture
            if(alreadyReported(userId)){
                colorIfNotReported();
            } else {
                colorIfReported();
            }
            String toastMessage = mDisplayedMedia.processReport(userId);
            ToastProvider.printOverCurrent(toastMessage, Toast.LENGTH_SHORT);
        }
    }

    /**
     * Checks if the user has upvoted the displayed picture
     * @param userID the user ID
     */
    private boolean alreadyUpvoted(String userID){
        if(mDisplayedMedia != null) {
            return mDisplayedMedia.getUpvotersList().contains(userID);
        } else {
            throw new NullPointerException("The photoObject is null");
        }
    }

    /**
     * Checks if the user has downvoted the displayed picture
     * @param userID the user ID
     */
    private boolean alreadyDownvoted(String userID){
        if(mDisplayedMedia != null){
            return mDisplayedMedia.getDownvotersList().contains(userID);
        } else {
            throw new NullPointerException("The photoObject is null");
        }
    }

    /**
     * Checks if the user has reported the displayed picture
     * @param userID the user ID
     */
    private boolean alreadyReported(String userID){
        if(mDisplayedMedia != null) {
            return mDisplayedMedia.getReportersList().contains(userID);
        } else {
            throw new NullPointerException("The photoObject is null");
        }
    }

    private void colorButtons(String userID){
        if(alreadyUpvoted(userID)){
            colorIfUpvote();
        } else if (alreadyDownvoted(userID)) {
            colorIfDownvote();
        }

        if(alreadyReported(userID)){
            colorIfReported();
        }
    }

    private void colorIfUpvote(){
        //Can't get the colors from resources because we are not in an activity and I can't have the context
        int colorGreen = Color.parseColor("#2ac903");
        int colorPrimary = Color.parseColor("#854442");
        mUpvoteButton.setBackgroundColor(colorGreen);
        mDownvoteButton.setBackgroundColor(colorPrimary);
    }

    private void colorIfDownvote(){
        int colorRed = Color.parseColor("#f20408");
        int colorPrimary = Color.parseColor("#854442");
        mUpvoteButton.setBackgroundColor(colorPrimary);
        mDownvoteButton.setBackgroundColor(colorRed);
    }

    private void colorIfReported(){
        int colorBlack = Color.parseColor("#000000");
        mReportButton.setBackgroundColor(colorBlack);
    }

    private void colorIfNotReported(){
        int colorPrimary = Color.parseColor("#854442");
        mReportButton.setBackgroundColor(colorPrimary);
    }
}
