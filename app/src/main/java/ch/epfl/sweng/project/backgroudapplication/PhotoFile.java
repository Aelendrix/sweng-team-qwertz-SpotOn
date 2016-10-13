package ch.epfl.sweng.project.backgroudapplication;

import java.io.File;
import java.sql.Timestamp;

/**
 * Created by Bruno on 11/10/2016.
 */

public class PhotoFile {
    private String mName;
    private Timestamp mCreationTimestamp;
    private Timestamp mDeletionTimestamp;
    private File mFile;

    public PhotoFile(String name, Timestamp creatingTime, Long durationInMs, File file){
        mName = name;
        mCreationTimestamp = creatingTime;
        mDeletionTimestamp = new Timestamp(creatingTime.getTime() + durationInMs);
        mFile = file;
    }

    public long getDeletionTime(){
        return mDeletionTimestamp.getTime();
    }

    public File getFile(){
        return mFile;
    }

    public void removeFile(){
        mFile.delete();
    }
}
