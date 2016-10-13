package ch.epfl.sweng.project.backgroudapplication;

import android.graphics.drawable.Drawable;

import java.io.File;
import java.sql.Timestamp;

/**
 * Created by Bruno on 11/10/2016.
 *
 * Represent a Photo (Drawable) with its deleting timestamp in function of its creating timestamp and its
 * life-duration.
 */

public class PhotoFile {
    private Timestamp mDeletionTimestamp;
    private Drawable mPhoto;

    /**
     *
     * @param photo the photo file
     * @param creatingTime the time at which the photo is created
     * @param durationInMs the photo's life-duration
     */
    public PhotoFile(Drawable photo, Timestamp creatingTime, Long durationInMs){
        mDeletionTimestamp = new Timestamp(creatingTime.getTime() + durationInMs);
        mPhoto = photo;
    }

    /**
     *
     * @return the timestamp at which the photo should be deleted
     */
    public long getDeletionTime(){
        return mDeletionTimestamp.getTime();
    }

    /**
     *
     * @return the photo file in itself as a Drawable
     */
    public Drawable getPhoto(){
        return mPhoto;
    }

}
