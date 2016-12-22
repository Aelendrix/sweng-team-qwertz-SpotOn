package ch.epfl.sweng.spotOn.gui;

import android.app.Activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.user.UserManager;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

/**
 * Class used to set the layout of the picture list in the profile activity
 */

public class PictureVoteListAdapter extends ArrayAdapter<PhotoObject> {

    public final static String EXTRA_USER_PICTURE_ID = "ch.epfl.sweng.spotOn.USER_PICTURE_ID";

    private final Activity mActivityContext;
    private List<PhotoObject> mPhotoList;
    private Bundle bundle = new Bundle();


    public PictureVoteListAdapter(Activity context, List<PhotoObject> photoList) {
        super(context, R.layout.content_profile_list_pictures, photoList);
        mActivityContext = context;
        mPhotoList = photoList;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mActivityContext.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.content_profile_list_pictures, null, true);
        TextView voteTextView = (TextView) rowView.findViewById(R.id.profilePictureVotes);

        final String pictureID = mPhotoList.get(position).getPictureId();

        final ImageView thumbnailImageView = (ImageView) rowView.findViewById(R.id.profilePictureThumbnail);
        thumbnailImageView.setImageBitmap(mPhotoList.get(position).getThumbnail());
        thumbnailImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToViewUserPictureActivity(mPhotoList.get(position).getPictureId());
            }
        });
        String vote = Integer.toString(mPhotoList.get(position).getUpvotes() - mPhotoList.get(position).getDownvotes());
        String textVote = voteTextView.getText() + " " + vote;
        voteTextView.setText(textVote);

        Button deleteButton = (Button) rowView.findViewById(R.id.deletePictureButton);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(UserManager.getInstance().getUser().retrieveUpdatedPhotosTaken().containsKey(pictureID)) {
                    /*LocalDatabase.getInstance().removePhotoObject(pictureID);
                    DatabaseRef.deletePhotoObjectFromDB(pictureID);
                    StorageRef.deletePictureFromStorage(pictureID);
                    UserManager.getInstance().getUser().removePhoto(pictureID);*/
                    bundle.putString("pictureID", pictureID);
                    deletePictureDialog();
                    mPhotoList.remove(mPhotoList.get(position));
                    LocalDatabase.getInstance().notifyListeners();

                }
                else {
                    ToastProvider.printOverCurrent("This picture has already been deleted, please refresh!", Toast.LENGTH_SHORT);
                }
                notifyDataSetChanged();
            }
        });

        return rowView;
    }


    public void goToViewUserPictureActivity(String pictureId){
        Intent intent = new Intent(mActivityContext, ViewUserPhotoActivity.class);
        intent.putExtra(EXTRA_USER_PICTURE_ID, pictureId);
        mActivityContext.startActivity(intent);
    }

    public void deletePictureDialog() {
        DialogFragment dialog = new DeletePictureDialog();
        dialog.setArguments(bundle);
        dialog.show(mActivityContext.getFragmentManager(), "DeletePicture");
    }
}
