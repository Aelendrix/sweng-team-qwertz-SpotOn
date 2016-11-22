package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.user.User;

/**
 * Created by Alexis Dewaele on 08/11/2016.
 */

public class FullScreenImageAdapter extends PagerAdapter {
    private Activity mActivity;

    private Map<String, PhotoObject> mPhotoMap;
    private List<String> mPhotosId;
    private List<PhotoObject> mPhotos;

    private ImageView mViewToSet;
    private PhotoObject mDisplayedMedia;

    public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID";
    private final static int RESOURCE_IMAGE_DOWNLOADING = R.drawable.image_downloading;
    private final static int RESOURCE_IMAGE_FAILURE =  R.drawable.image_failure;

    public FullScreenImageAdapter(Activity activity) {
        mActivity = activity;
        mPhotoMap = LocalDatabase.getInstance().getViewableMedias();
        mPhotosId = new ArrayList<>(mPhotoMap.keySet());
        mPhotos = new ArrayList<>(mPhotoMap.values());
    }

    @Override
    public int getCount() {
        return mPhotos.size();
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

        Intent displayImageIntent = mActivity.getIntent();
        final String wantedImagePictureId = displayImageIntent.getExtras().getString(WANTED_IMAGE_PICTUREID);

        if(mPhotoMap.containsKey(wantedImagePictureId)){
            Log.d("ViewFullsizeImageAct.", "Error : local copy of database has no matching object for ID "+ wantedImagePictureId);
            mViewToSet.setImageResource(RESOURCE_IMAGE_FAILURE);
            if(LocalDatabase.getInstance().hasKey(wantedImagePictureId)){
                Log.d("ViewFullsizeImageAct.", "Localdatabase does, though");
                throw new IllegalStateException("Wanted object not in local copy of database (but exists in localdatabase)");
            }
            throw new IllegalStateException("Wanted object not in local copy of database (and not in localdatabase)");
        }else {
            mDisplayedMedia = mPhotos.get(position);
            Bitmap imageToDisplay = null;
            if (mDisplayedMedia.hasFullSizeImage()) {
                imageToDisplay = mDisplayedMedia.getFullSizeImage();
                mViewToSet.setImageBitmap(imageToDisplay);
            } else {
                // retrieveFullsizeImage throws an IllegalArgumentException if mFullsizeImageLink isn't a valid firebase link
                try {
                    // add a listener that will set the image when it is retrieved
                    mDisplayedMedia.retrieveFullsizeImage(true, newImageViewSetterListener());
                }catch (IllegalArgumentException e){
                    mViewToSet.setImageResource(RESOURCE_IMAGE_FAILURE);
                    Log.d("Error", "couldn't retrieve fullsizeImage from fileserver for Object with ID"+wantedImagePictureId);
                }
            }
        }

        container.addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
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
            String toastMessage = mDisplayedMedia.processVote(vote, userId);
            Toast.makeText(mActivity, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }


    public void reportOffensivePicture(View view){
        if(mDisplayedMedia == null) {
            Log.e("FullScreenImageAdapter","reportOffensivePicture mDisplayedMedia is null");
        }else{
            String userId = User.getInstance().getUserId();
            String toastMessage = mDisplayedMedia.processReport(userId);
            Toast.makeText(mActivity, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
