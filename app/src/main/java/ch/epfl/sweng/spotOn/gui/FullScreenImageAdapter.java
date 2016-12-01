package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
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

import java.util.NoSuchElementException;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.user.UserManager;
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

        ImageButton upvote = (ImageButton) viewLayout.findViewById(R.id.upvoteButton);
        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDisplayedMedia==null) {
                    throw new NullPointerException("FullScreenImageAdapter : trying to vote on a null media");
                }else {
                    String userId = UserManager.getInstance().getUser().getUserId();
                    //fake vote method to have more responsive interface
                    if (!mDisplayedMedia.getAuthorId().equals(userId) && !mDisplayedMedia.getUpvotersList().contains(userId)) {
                        voteSum++;
                        if (mDisplayedMedia.getDownvotersList().contains(userId)) {
                            voteSum++;
                        }
                    }
                    refreshVoteTextView(Integer.toString(voteSum));

                    String toastMessage = mDisplayedMedia.processVote(1, userId);
                    ToastProvider.printOverCurrent(toastMessage, Toast.LENGTH_SHORT);
                }
            }
        });

        ImageButton downVote = (ImageButton) viewLayout.findViewById(R.id.downvoteButton);
        downVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDisplayedMedia == null) {
                    throw new NullPointerException("FullScreenImageAdapter : trying to vote on a null media");
                } else {
                    String userId = UserManager.getInstance().getUser().getUserId();
                    //fake vote method to have more responsive interface
                    if (!mDisplayedMedia.getAuthorId().equals(userId) && !mDisplayedMedia.getUpvotersList().contains(userId)) {
                        voteSum--;
                        if (mDisplayedMedia.getDownvotersList().contains(userId)) {
                            voteSum--;
                        }
                    }
                    refreshVoteTextView(Integer.toString(voteSum));

                    String toastMessage = mDisplayedMedia.processVote(-1, userId);
                    ToastProvider.printOverCurrent(toastMessage, Toast.LENGTH_SHORT);
                }
            }
        });
        container.addView(viewLayout);
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
            String userId = UserManager.getInstance().getUser().getUserId();
            //fake vote method to have more responsive interface
            if(vote==1&&!mDisplayedMedia.getAuthorId().equals(userId)&&!mDisplayedMedia.getUpvotersList().contains(userId)){
                voteSum++;
                if(mDisplayedMedia.getDownvotersList().contains(userId)){
                    voteSum++;
                }
            }
            if(vote==-1&&!mDisplayedMedia.getAuthorId().equals(userId)&&!mDisplayedMedia.getDownvotersList().contains(userId)){
                voteSum--;
                if(mDisplayedMedia.getUpvotersList().contains(userId)){
                    voteSum--;
                }
            }
            refreshVoteTextView(Integer.toString(voteSum));

            String toastMessage = mDisplayedMedia.processVote(vote, userId);
            ToastProvider.printOverCurrent(toastMessage, Toast.LENGTH_SHORT);
        }
    }


    public void reportOffensivePicture(View view){
        if(mDisplayedMedia == null) {
            Log.e("FullScreenImageAdapter","reportOffensivePicture mDisplayedMedia is null");
        }else{
            String userId = UserManager.getInstance().getUser().getUserId();
            String toastMessage = mDisplayedMedia.processReport(userId);
            ToastProvider.printOverCurrent(toastMessage, Toast.LENGTH_SHORT);
        }
    }
}
