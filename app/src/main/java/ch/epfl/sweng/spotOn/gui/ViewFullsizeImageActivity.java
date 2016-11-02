package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fullsize_image);

        mViewToSet = (ImageView) findViewById(R.id.fullSizeImageView);
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
                    mDisplayedMedia.retrieveFullsizeImage(true, newImageViewSetterListener(), true, newFailureImageSetterListener());
                }catch (IllegalArgumentException e){
                    mViewToSet.setImageResource(RESOURCE_IMAGE_FAILURE);
                    Log.d("Error", "couldn't retrieve fullsizeImage from fileserver for Object with ID"+wantedImagePictureId);
                }
            }
        }
    }

    /** Factory method that returns a listener that
     * sets the imageView with the result of its query
     */
    private OnSuccessListener newImageViewSetterListener(){
        return new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] imageAsByteArray) {
                Bitmap obtainedImage = BitmapFactory.decodeByteArray(imageAsByteArray, 0, imageAsByteArray.length);
                mViewToSet.setImageBitmap(obtainedImage);
            }
        };
    }
    /** Factory method which creates a Listener that,
     * in case of failure, displays the received exception on console and sets the image displayed on view to an errorImage
     */
    private OnFailureListener newFailureImageSetterListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Exception", e.toString());
                mViewToSet.setImageResource(RESOURCE_IMAGE_FAILURE);
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
        if(mDisplayedMedia!=null){
            String userId = UserId.getInstance().getUserId();
            if(! mDisplayedMedia.getVoters().contains(userId)) {
                mDisplayedMedia.vote(vote);
                String toastText;
                if(vote == 1){
                    toastText = "Upvoted";
                }
                else{
                    toastText = "Downvoted";
                }
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
            }
            else if(mDisplayedMedia.getAuthorId().equals(userId)){
                Toast.makeText(this, "You can't vote for your own photo!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "You've already voted for this picture!", Toast.LENGTH_LONG).show();
            }
        }
        else{
            throw new NullPointerException();
        }
    }
}
