package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.user.UserId;

public class ViewFullsizeImageActivity extends Activity {

    public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID";
    private final static int RESOURCE_IMAGE_DOWNLOADING = R.drawable.image_downloading;
    private final static int RESOURCE_IMAGE_FAILURE =  R.drawable.image_failure;

    private PhotoObject mDisplayedMedia = null;
    private ImageView mViewToSet;

    private FullScreenImageAdapter mFullScreenImageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fullsize_image);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        mFullScreenImageAdapter = new FullScreenImageAdapter(this);
        viewPager.setAdapter(mFullScreenImageAdapter);
        Intent displayImageIntent = getIntent();
        int position = displayImageIntent.getIntExtra("position", SeePicturesFragment.mPosition);
        viewPager.setCurrentItem(position);

        /*mViewToSet = (ImageView) findViewById(R.id.fullSizeImageView);
        mViewToSet.setImageResource(RESOURCE_IMAGE_DOWNLOADING);

        Intent displayImageIntent = getIntent();
        final String wantedImagePictureId = displayImageIntent.getExtras().getString(WANTED_IMAGE_PICTUREID);

        if(!LocalDatabase.hasKey(wantedImagePictureId)){
            Log.d("Error", "ViewFullsizeImageActivity : LocalDatabase has no matching object for ID "+ wantedImagePictureId);
            mViewToSet.setImageResource(RESOURCE_IMAGE_FAILURE);
        }else {
            mDisplayedMedia = LocalDatabase.getPhoto(wantedImagePictureId);
            Bitmap imageToDisplay = null;
            if (mDisplayedMedia.hasFullSizeImage()) {
                imageToDisplay = mDisplayedMedia.getFullSizeImage();
                mViewToSet.setImageBitmap(imageToDisplay);
            } else {
                // retrieveFullsizeImage throws an IllegalArgumentExceptino if mFullsizeImageLink isn't a valid firebase link
                try {
                    // add a listener that will set the image when it is retrieved
                    mDisplayedMedia.retrieveFullsizeImage(true, newImageViewSetterListener());
                }catch (IllegalArgumentException e){
                    mViewToSet.setImageResource(RESOURCE_IMAGE_FAILURE);
                    Log.d("Error", "couldn't retrieve fullsizeImage from fileserver for Object with ID"+wantedImagePictureId);
                }
            }
        }*/
    }

    /** Factory method that returns a listener that
     * sets the imageView with the result of its query
     * or deals with errors if need be
     */
    private OnCompleteListener<byte[]> newImageViewSetterListener(){
        return new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> uploadMediaTask) {
                if(uploadMediaTask.getException()!=null){
                    throw new Error("Uploading media failed");
                }else{
                    Bitmap obtainedImage = BitmapFactory.decodeByteArray(uploadMediaTask.getResult(), 0, uploadMediaTask.getResult().length);
                    mViewToSet.setImageBitmap(obtainedImage);
                }
            }
        };
    }

    public void recordUpvote(View view) {
        mFullScreenImageAdapter.recordUpvote(view);
    }

    public void recordDownvote(View view) {
        mFullScreenImageAdapter.recordDownvote(view);
    }

/*
    public void recordUpvote(View view){
        vote(1);
    }

    public void recordDownvote(View view){
        vote(-1);
    }

    private void vote(int vote){
        if(mDisplayedMedia==null) {
            throw new NullPointerException();
        }else{
            String userId = UserId.getInstance().getUserId();
            String toastMessage = mDisplayedMedia.processVote(vote, userId);
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }
    */
}
