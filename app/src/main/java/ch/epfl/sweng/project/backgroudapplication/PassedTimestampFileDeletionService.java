package ch.epfl.sweng.project.backgroudapplication;

import android.app.IntentService;
import android.content.Intent;

import java.sql.Timestamp;
import java.util.List;

import ch.epfl.sweng.project.PictureActivity;


/**
 * Created by Bruno on 11/10/2016.
 */

public class PassedTimestampFileDeletionService extends IntentService {
    private Timestamp mTimestamp;

    public PassedTimestampFileDeletionService(){
        super("Deletes files which timestamp value has passed");
        mTimestamp = new Timestamp(System.currentTimeMillis());
    }

    /**
     *
     * @param intent: the intent that started the Service.
     *
     * Runs in backgroud and control each Photo timestamp value. If this becomes smaller than the
     * present timestamp, the Photo is deleted.
     *
     */
    @Override
    protected void onHandleIntent(Intent intent){
        /*
        While there are photos in the list, we check for each one if its
        delete timestamp is not already passed. If so, we delete it.
         */
        while(PictureActivity.mSavedPhotos.size() > 0){
            mTimestamp.setTime(System.currentTimeMillis());
            List<PhotoFile> photos = PictureActivity.mSavedPhotos.getPhotos();
            for(int i = 0; i < PictureActivity.mSavedPhotos.size(); ++i){
                if(photos.get(i).getDeletionTime() < mTimestamp.getTime()){
                    PictureActivity.mSavedPhotos.deletePhoto(i);
                }
            }
        }
    }

}
