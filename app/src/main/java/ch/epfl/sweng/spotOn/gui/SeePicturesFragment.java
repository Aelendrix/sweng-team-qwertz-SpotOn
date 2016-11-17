package ch.epfl.sweng.spotOn.gui;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabaseListener;

public class SeePicturesFragment extends Fragment implements LocalDatabaseListener{

    View mView;
    GridView mGridView;
    private ImageAdapter mImageAdapter;
    protected static int mDefaultItemPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_see_pictures, container, false);
        mGridView = (GridView) mView.findViewById(R.id.gridview);
        mImageAdapter = new ImageAdapter(mView.getContext());
        mGridView.setAdapter(mImageAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                mDefaultItemPosition = position;
                displayFullsizeImage(position);
                Log.d("Grid","matching pictureId : " + mImageAdapter.getIdAtPosition(position));
            }
        });
        return mView;
    }

    //refresh the Grid when called
    public void refreshGrid(){
        if(mGridView!=null&&mView!=null){
                    //create a new adapter and refresh the gridView
                    mImageAdapter= new ImageAdapter(mView.getContext());
                    mGridView.invalidateViews();
                    mGridView.setAdapter(mImageAdapter);
                    LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.empty_grid_info);
                    if(mImageAdapter.getCount()==0){
                        mGridView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        linearLayout.setVisibility(View.GONE);
                        mGridView.setVisibility(View.VISIBLE);
                    }

        }

    }

    /**  launches the fullSizeImageViewActivity and displays the thumbnail that has been clicked (method called by a OnClickListener in the gridview
     */
    public void displayFullsizeImage(int positionOfThumbnail){
        Intent displayFullsizeImageIntent = new Intent(this.getActivity(), ViewFullsizeImageActivity.class);
        displayFullsizeImageIntent.putExtra(ViewFullsizeImageActivity.WANTED_IMAGE_PICTUREID, mImageAdapter.getIdAtPosition(positionOfThumbnail));
        startActivity(displayFullsizeImageIntent);
    }

    @Override
    public void databaseUpdated() {
        refreshGrid();
    }
}
