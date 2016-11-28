package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;


import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.media.PhotoObject;

public class ViewFullsizeImageActivity extends Activity {

//    public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID";

    private FullScreenImageAdapter mFullScreenImageAdapter;
    private ImageButton upvoteButton;
    private ImageButton downvoteButton;


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
        upvoteButton = (ImageButton) findViewById(R.id.upvoteButton);
        downvoteButton = (ImageButton) findViewById(R.id.downvoteButton);
    }


    public void recordUpvote(View view) {
        mFullScreenImageAdapter.recordUpvote(view);
        if(mFullScreenImageAdapter.getUpvoted()){
            upvoteButton.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.button_shape_upvote_clicked));
            downvoteButton.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.button_shape_downvote));
        }
    }


    public void recordDownvote(View view) {
        mFullScreenImageAdapter.recordDownvote(view);
        if(mFullScreenImageAdapter.getDownvoted()){
            downvoteButton.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.button_shape_downvote_clicked));
            upvoteButton.setBackground(ContextCompat.getDrawable(this,
                    R.drawable.button_shape_upvote));
        }
    }


    public void reportOffensivePicture(View view) {
        mFullScreenImageAdapter.reportOffensivePicture(view);
    }
}
