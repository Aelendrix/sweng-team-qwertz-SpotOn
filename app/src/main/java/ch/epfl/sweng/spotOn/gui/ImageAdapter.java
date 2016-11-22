package ch.epfl.sweng.spotOn.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;

/**
 * This class is the core of the gridView, used to link the data to one of the grid object
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    private Map<String,Bitmap> mThumbnailMap;
    private List<String> mThumbId;
    private List<Bitmap> mThumbnail;


    public ImageAdapter(Context c) {
        mThumbnailMap = LocalDatabase.getInstance().getViewableThumbnails();
        mThumbId = new ArrayList<>(mThumbnailMap.keySet());
        mThumbnail = new ArrayList<>();
        mContext = c;
        for(String s: mThumbId){
            mThumbnail.add(mThumbnailMap.get(s));
        }

    }

    public int getCount() {
        return mThumbnail.size();
    }

    public Object getItem(int position) {
        return mThumbnail.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView sImageView;
        if (convertView == null) {
            // attribute of one object in the grid
            sImageView = new SquareImageView(mContext);
            sImageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
            sImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            sImageView = (SquareImageView) convertView;
        }

        sImageView.setImageBitmap(mThumbnail.get(position));
        return sImageView;
    }

    public String getIdAtPosition(int pos){
        return mThumbId.get(pos);
    }

    public int size(){
        return mThumbId.size();
    }
}