package ch.epfl.sweng.spotOn.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;

/**
 * This class is the core of the gridView, used to link the data to one of the grid object
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    private Map<String,PhotoObject> mMediaMap;
    private List<String> mThumbId;
    private List<Bitmap> mThumbnail;


    public ImageAdapter(Context c, int ordering) {
        mMediaMap = LocalDatabase.getInstance().getViewableMedias();
        mThumbId = new ArrayList<>(mMediaMap.keySet());
        //order the list in function of ordering

        //Most positive voting appear first
        Comparator<String> mostUpVoteComparator = new Comparator<String>(){
            @Override
            public int compare(String objectId1, String objectId2)
            {
                int vote1 = mMediaMap.get(objectId1).getUpvotes()-mMediaMap.get(objectId1).getDownvotes();
                int vote2 = mMediaMap.get(objectId2).getUpvotes()-mMediaMap.get(objectId2).getDownvotes();
                return ((Integer)vote2).compareTo(vote1);
            }
        };

        //The most voted picture appear first
        Comparator<String> hottestComparator = new Comparator<String>(){
            @Override
            public int compare(String objectId1, String objectId2)
            {
                int vote1 = mMediaMap.get(objectId1).getUpvotes()+mMediaMap.get(objectId1).getDownvotes();
                int vote2 = mMediaMap.get(objectId2).getUpvotes()+mMediaMap.get(objectId2).getDownvotes();
                return ((Integer)vote2).compareTo(vote1);
            }
        };

        //oldest picture first
        Comparator<String> oldestComparator = new Comparator<String>(){
            @Override
            public int compare(String objectId1, String objectId2)
            {
                return mMediaMap.get(objectId1).getCreatedDate().compareTo(mMediaMap.get(objectId2).getCreatedDate());
            }
        };

        //newest picture first
        Comparator<String> newestComparator = new Comparator<String>(){
            @Override
            public int compare(String objectId1, String objectId2)
            {
                return mMediaMap.get(objectId2).getCreatedDate().compareTo(mMediaMap.get(objectId1).getCreatedDate());
            }
        };

        //default comparator, ordering the string ID (which is equivalent to oldestComparator since we use firebase)
        Comparator<String> defaultComparator = new Comparator<String>(){
            @Override
            public int compare(String objectId1, String objectId2)
            {
                return objectId1.compareTo(objectId2);
            }
        };

        Comparator<String> currentComparator;

        switch(ordering){
            case SeePicturesFragment.UPVOTE_ORDER:
                currentComparator = mostUpVoteComparator;
                break;
            case SeePicturesFragment.HOTTEST_ORDER:
                currentComparator = hottestComparator;
                break;
            case SeePicturesFragment.NEWEST_ORDER:
                currentComparator = newestComparator;
                break;
            case SeePicturesFragment.OLDEST_ORDER:
                currentComparator = oldestComparator;
                break;
            case SeePicturesFragment.DEFAULT_ORDER:
            default:
                currentComparator = defaultComparator;
                break;
        }
        Collections.sort(mThumbId,currentComparator);

        mThumbnail = new ArrayList<>();
        mContext = c;
        for(String s: mThumbId){
            mThumbnail.add(mMediaMap.get(s).getThumbnail());
        }

    }

    @Override
    public int getCount() {
        return mThumbnail.size();
    }

    @Override
    public Object getItem(int position) {
        return mThumbnail.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
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

    public boolean containsThumbID(String thumbID){
        return mThumbId.contains(thumbID);
    }

    public int getPositionThumbID(String thumbID){
        return mThumbId.indexOf(thumbID);
    }

    public String getIdAtPosition(int pos){
        return mThumbId.get(pos);
    }

    public int size(){
        return mThumbId.size();
    }

}