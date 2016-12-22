package ch.epfl.sweng.spotOn.gui;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.media.PhotoObjectStoredInDatabase;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

public class ViewUserPhotoActivity extends Activity {

    private final static int RESOURCE_IMAGE_DOWNLOADING = R.mipmap.image_downloading;
    private final static int RESOURCE_IMAGE_FAILURE =  R.mipmap.image_failure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_picture);

        Intent intent = getIntent();
        final String pictureId = intent.getStringExtra(PictureVoteListAdapter.EXTRA_USER_PICTURE_ID);

        final ImageView userPicture = (ImageView) findViewById(R.id.imageViewUserPicture);
        userPicture.setImageResource(RESOURCE_IMAGE_DOWNLOADING);

        final LocalDatabase localDB= LocalDatabase.getInstance();
        boolean retrieveFromServer = true;
        if(localDB.hasKey(pictureId)){
            if(localDB.get(pictureId).hasFullSizeImage()) {
                userPicture.setImageBitmap(localDB.get(pictureId).getFullSizeImage());
                retrieveFromServer = false;
            }
        }
        if(retrieveFromServer) {
            Query photoByPictureId = DatabaseRef.getMediaDirectory().orderByChild("pictureId").equalTo(pictureId);
            photoByPictureId.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                        if (photoSnapshot.getKey().equals(pictureId)) {
                            PhotoObject photoObject = photoSnapshot.getValue(PhotoObjectStoredInDatabase.class).convertToPhotoObject();
                            photoObject.retrieveFullsizeImage(true, new OnCompleteListener<byte[]>() {
                                @Override
                                public void onComplete(@NonNull Task<byte[]> retrieveFullSizePicTask) {
                                    if (retrieveFullSizePicTask.getException() != null) {
                                        userPicture.setImageResource(RESOURCE_IMAGE_FAILURE);
                                        throw new Error("ViewUserPhotoActivity : Retrieving fullSizePicture with pictureId : \n" + pictureId + "failed due to :\n " + retrieveFullSizePicTask.getException());

                                    } else {
                                        Bitmap obtainedImage = BitmapFactory.decodeByteArray(retrieveFullSizePicTask.getResult(), 0, retrieveFullSizePicTask.getResult().length);
                                        userPicture.setImageBitmap(obtainedImage);
                                    }
                                }
                            });
                            //If there is a photoObject with this picture Id, we delete it from local database because it doesn't have
                            //a full-size picture (else it would have retrieved from database)
                            localDB.removePhotoObject(pictureId);
                            localDB.addPhotoObject(photoObject);
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
