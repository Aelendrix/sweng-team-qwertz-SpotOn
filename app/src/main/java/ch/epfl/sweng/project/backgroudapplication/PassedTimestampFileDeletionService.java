package ch.epfl.sweng.project.backgroudapplication;

import android.app.IntentService;
import android.content.Intent;

import java.sql.Timestamp;
import java.util.List;


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
        PhotoList photoFiles = new PhotoList();
        int photoListSize = photoFiles.size();

        while(photoListSize > 0){
            mTimestamp = new Timestamp(System.currentTimeMillis());
            List<PhotoFile> photos = photoFiles.getPhotos();
            for(int i = 0; i < photoListSize; ++i){
                if(photos.get(i).getDeletionTime() < mTimestamp.getTime()){
                    photoFiles.deletePhoto(i);
                }
            }
            photoFiles = new PhotoList();
            photoListSize = photoFiles.size();
        }
    }

}
