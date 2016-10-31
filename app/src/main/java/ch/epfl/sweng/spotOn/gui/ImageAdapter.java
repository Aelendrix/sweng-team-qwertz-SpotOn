package ch.epfl.sweng.spotOn.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.util.Pair;

/**
 * This class is the core of the gridView, used to link the data to one of the grid object
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Pair<Bitmap, String>> mThumbnail = LocalDatabase.getThumbnailArray();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbnail.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // attribute of one object in the grid
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(300,300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(mThumbnail.get(position)._1());
        return imageView;
    }

    public String getIdAtPosition(int pos){
        return mThumbnail.get(pos)._2();
    }
}