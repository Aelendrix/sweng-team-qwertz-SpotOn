package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

public class ViewFullsizeImageActivity extends Activity {

    //public final static String WANTED_IMAGE_PICTUREID = "ch.epfl.sweng.teamqwertz.spoton.ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID";

    private FullScreenImageAdapter mFullScreenImageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_fullsize_image);

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
                mFullScreenImageAdapter.setCurrentMedia(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Intent displayImageIntent = getIntent();
        int position = displayImageIntent.getIntExtra("position", SeePicturesFragment.mDefaultItemPosition);
        viewPager.setCurrentItem(position);
    }


    public void recordUpvote(View view) {
        if( !UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(User.NOT_LOGGED_in_MESSAGE, Toast.LENGTH_SHORT);
        }else {
            mFullScreenImageAdapter.recordUpvote(view);
        }
    }


    public void recordDownvote(View view) {
        if( !UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(User.NOT_LOGGED_in_MESSAGE, Toast.LENGTH_SHORT);
        }else {
            mFullScreenImageAdapter.recordDownvote(view);
        }
    }


    public void reportOffensivePicture(View view) {
        if( !UserManager.getInstance().userIsLoggedIn() ){
            ToastProvider.printOverCurrent(User.NOT_LOGGED_in_MESSAGE, Toast.LENGTH_SHORT);
        }else {
            mFullScreenImageAdapter.reportOffensivePicture(view);
        }
    }

}
