package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.media.PhotoObject;

/**
 * Class used to set the layout of the picture list in the profile activity
 */

public class PictureVoteListAdapter extends ArrayAdapter<PhotoObject> {

    private final Activity mActivityContext;
    private List<PhotoObject> mPhotoList;

    public PictureVoteListAdapter(Activity context, List<PhotoObject> photoList) {
        super(context, R.layout.content_profile_list_pictures, photoList);
        mActivityContext = context;
        mPhotoList = photoList;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mActivityContext.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.content_profile_list_pictures, null, true);
        TextView voteTextView = (TextView) rowView.findViewById(R.id.profilePictureVotes);

        ImageView thumbnailImageView = (ImageView) rowView.findViewById(R.id.profilePictureThumbnail);
        thumbnailImageView.setImageBitmap(mPhotoList.get(position).getThumbnail());
        String vote = Integer.toString(mPhotoList.get(position).getUpvotes() - mPhotoList.get(position).getDownvotes());
        String textVote = voteTextView.getText() + " " + vote;
        voteTextView.setText(textVote);

        return rowView;
    }
}
