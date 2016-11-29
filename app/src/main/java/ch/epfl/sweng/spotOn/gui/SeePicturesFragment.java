package ch.epfl.sweng.spotOn.gui;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabaseListener;

public class SeePicturesFragment extends Fragment implements LocalDatabaseListener{

    public final static int DEFAULT_ORDER=0;
    public final static int UPVOTE_ORDER=1;
    public final static int OLDEST_ORDER=2;
    public final static int NEWEST_ORDER=3;
    public final static int HOTTEST_ORDER=4;
    View mView;
    GridView mGridView;
    private static ImageAdapter mImageAdapter;
    protected static int mDefaultItemPosition = 0;
    int mOrdering = DEFAULT_ORDER;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LocalDatabase.getInstance().addListener(this);

        mView = inflater.inflate(R.layout.activity_see_pictures, container, false);
        mGridView = (GridView) mView.findViewById(R.id.gridview);
        mImageAdapter = new ImageAdapter(mView.getContext(),0);
        mGridView.setAdapter(mImageAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                mDefaultItemPosition = position;
                displayFullsizeImage(position);
                Log.d("Grid","matching pictureId : " + mImageAdapter.getIdAtPosition(position));
            }
        });
        refreshGrid(mOrdering);
        return mView;
    }

    /*refresh the Grid when called
    @param ordering : represent the order of the image in the grid, value is initialised by the static constant in this class
    */
    public void refreshGrid(int ordering){
        if(mGridView!=null&&mView!=null){
            mOrdering=ordering;
            mImageAdapter= new ImageAdapter(mView.getContext(), mOrdering);
            mGridView.invalidateViews();
            mGridView.setAdapter(mImageAdapter);
            if(getActivity()==null){
                Log.d("Correct tab", "Not on Around me tab");
            }else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //create a new adapter and refresh the gridView

                        LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.empty_grid_info);
                        FrameLayout frameLayout = (FrameLayout) mView.findViewById(R.id.grid_layout);
                        if (mImageAdapter.getCount() == 0) {
                            frameLayout.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);
                        } else {
                            linearLayout.setVisibility(View.GONE);
                            frameLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    }

    /**  launches the fullSizeImageViewActivity and displays the thumbnail that has been clicked (method called by a OnClickListener in the gridview
     */
    public void displayFullsizeImage(int positionOfThumbnail){
        Intent displayFullsizeImageIntent = new Intent(this.getActivity(), ViewFullsizeImageActivity.class);
//        displayFullsizeImageIntent.putExtra(ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID, mImageAdapter.getIdAtPosition(positionOfThumbnail));
        startActivity(displayFullsizeImageIntent);
    }

    @Override
    public void databaseUpdated() {
        refreshGrid(mOrdering);
    }

    public static ImageAdapter getImageAdapter(){
        if(mImageAdapter==null){
            throw new IllegalStateException("Activity doesn't have an imageAdapter yet");
        }
        return mImageAdapter;
    }
}
