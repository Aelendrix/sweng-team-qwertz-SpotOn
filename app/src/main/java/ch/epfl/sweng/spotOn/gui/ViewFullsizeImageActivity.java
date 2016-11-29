package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;


import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.media.PhotoObject;

public class ViewFullsizeImageActivity extends Activity {

//    public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID";

    private FullScreenImageAdapter mFullScreenImageAdapter;
    private ImageButton mUpvoteButton;
    private ImageButton mDownvoteButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fullsize_image);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        mFullScreenImageAdapter = new FullScreenImageAdapter(this);
        viewPager.setAdapter(mFullScreenImageAdapter);
        Intent displayImageIntent = getIntent();
        int position = displayImageIntent.getIntExtra("position", SeePicturesFragment.mDefaultItemPosition);
        viewPager.setCurrentItem(position);

        mUpvoteButton = (ImageButton) findViewById(R.id.upvoteButton);
        mDownvoteButton = (ImageButton) findViewById(R.id.downvoteButton);

        Log.d("instantiateItem", "called2");
        if(mFullScreenImageAdapter.getUpvoted()){
            colorUpvote();
        } else if(mFullScreenImageAdapter.getDownvoted()){
            colorDownvote();
        } else {
            colorNone();
        }
    }

    /**
     * Records an upvote and change the color of the upvote button if the user hasnt upvoted
     * the picture yet
     * @param view
     */
    public void recordUpvote(View view) {
        mFullScreenImageAdapter.recordUpvote(view);
        colorUpvote();
    }

    /**
     * Records a downvote and change the color of the downvote button if the user hasnt downvoted
     * the picture yet
     * @param view
     */
    public void recordDownvote(View view) {
        mFullScreenImageAdapter.recordDownvote(view);
        colorDownvote();
    }

    public void reportOffensivePicture(View view) {
        mFullScreenImageAdapter.reportOffensivePicture(view);
    }

    /**
     * Colors upvote button in green -> downvote button has to be normal
     */
    public void colorUpvote(){
        mUpvoteButton.setBackground(ContextCompat.getDrawable(this,
                R.drawable.button_shape_upvote_clicked));
        mDownvoteButton.setBackground(ContextCompat.getDrawable(this,
                R.drawable.button_shape_downvote));
    }

    /**
     * Colors downvote button in red -> upvote button has to be normal
     */
    public void colorDownvote(){
        mDownvoteButton.setBackground(ContextCompat.getDrawable(this,
                R.drawable.button_shape_downvote_clicked));
        mUpvoteButton.setBackground(ContextCompat.getDrawable(this,
                R.drawable.button_shape_upvote));
    }

    /**
     * Colors both downvote and upvote in their natural color (if the user hasn't vote for the picture
     */
    public void colorNone(){
        mUpvoteButton.setBackground(ContextCompat.getDrawable(this,
                R.drawable.button_shape_upvote));
        mDownvoteButton.setBackground(ContextCompat.getDrawable(this,
                R.drawable.button_shape_downvote));
    }
}
