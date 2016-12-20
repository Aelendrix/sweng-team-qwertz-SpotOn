package ch.epfl.sweng.spotOn.test.util;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseException;

import java.io.IOError;
import java.io.IOException;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;

/**
 * Created by olivi on 07.12.2016.
 *
 * Created this class to refactor some code for the tests when we initialize the local database
 * and erase the photo objects from it at the end of each tests
 */

public class LocalDatabaseTestUtils {
    private static PhotoObject firstPo=null;
    private static PhotoObject secondPo=null;

    public static void initLocalDatabase(boolean onlyOnePhoto) throws InterruptedException {
        ServicesChecker.allowDisplayingToasts(false);
        Location location = new Location("testLocationProvider");
        location.setLatitude(0.52890355757567);
        location.setLongitude(0.569420238493345);
        location.setAltitude(0);
        location.setTime(System.currentTimeMillis());

        TestInitUtils.initContext(location);

        final Object lock1 = new Object();
        firstPo = PhotoObjectTestUtils.germaynDeryckePO();

        firstPo.upload(true, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.getException()!=null){
                    lock1.notify();
                    throw new IOError(new IOException("LocalDatabaseTestUtils : ERROR - uploading first testPhotoObject failed"));
                }else{
                    synchronized (lock1){
                        lock1.notify();
                    }
                }
            }
        });
        synchronized (lock1)
        {lock1.wait();}
        LocalDatabase.getInstance().addPhotoObject(firstPo);

        if( ! onlyOnePhoto ) {
            final Object lock2 = new Object();
            secondPo = PhotoObjectTestUtils.paulVanDykPO();
            secondPo.upload(true, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.getException() != null) {
                        lock2.notify();
                        throw new IOError(new IOException("LocalDatabaseTestUtils : ERROR - uploading second testPhotoObject failed"));
                    } else {
                        synchronized (lock2) {
                            lock2.notify();
                        }
                    }
                }
            });
            synchronized (lock2) {
                lock2.wait();
            }
            LocalDatabase.getInstance().addPhotoObject(secondPo);
        }


    }

    public static void afterTests(){
        ConcreteLocationTracker.destroyInstance();
        if( ConcreteLocationTracker.instanceExists()){
            throw new AssertionError("CameraTest : concreteLocationTracker mock instance not deleted : "+ConcreteLocationTracker.getInstance().getLocation());
        }
        // stuff below seems pretty unimportant to me
        if(LocalDatabase.instanceExists()){
            if(firstPo!=null){
                DatabaseRef.deletePhotoObjectFromDB(firstPo.getPictureId());
                StorageRef.deletePictureFromStorage(firstPo.getPictureId());
            }
            if(secondPo!=null){
                DatabaseRef.deletePhotoObjectFromDB(secondPo.getPictureId());
                StorageRef.deletePictureFromStorage(secondPo.getPictureId());

            }
        }
    }
}
