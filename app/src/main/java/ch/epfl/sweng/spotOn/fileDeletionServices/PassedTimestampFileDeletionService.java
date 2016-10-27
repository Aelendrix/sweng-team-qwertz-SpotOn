package ch.epfl.sweng.spotOn.fileDeletionServices;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



/**
 * Created by Bruno on 11/10/2016.
 */

public class PassedTimestampFileDeletionService extends IntentService {

    final long timeToKeepFile = 60000; //1 minute for example
    public PassedTimestampFileDeletionService(){
        super("Deletes files which timestamp value has passed");
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
    protected void onHandleIntent(Intent intent)
    {
        File folder = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                "/SpotOn/Pictures").getPath());
        List<File> listOfFiles = Collections.emptyList();
        if(folder.listFiles() != null) {
            listOfFiles = Arrays.asList(folder.listFiles());
        }

        while(folder.exists()) {
            if(! listOfFiles.isEmpty()) {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        if (getTimestamp(file) < System.currentTimeMillis() - timeToKeepFile) {
                            file.delete();
                        }
                    }
                }
            }
            if(folder.listFiles() == null){
                listOfFiles = Collections.emptyList();
            }
            else {
                listOfFiles = Arrays.asList(folder.listFiles());
            }
            /*Sleep the service for 1 second after each control to have only 1 control per second
            If we don't do that, there are more than 2000 controls each second and uses a lot of RAM
            and battery
             */
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private long getTimestamp(File file){return file.lastModified();}
}
