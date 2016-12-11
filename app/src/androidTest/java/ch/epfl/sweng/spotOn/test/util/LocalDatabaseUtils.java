package ch.epfl.sweng.spotOn.test.util;

import android.location.Location;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;

/**
 * Created by olivi on 07.12.2016.
 *
 * Created this class to refactor some code for the tests when we initialize the local database
 * and erase the photo objects from it at the end of each tests
 */

public class LocalDatabaseUtils {
    private static PhotoObject firstPo=null;
    private static PhotoObject secondPo=null;

    public static void initLocalDatabase(boolean onlyOnePhoto) throws InterruptedException {
        Location location = new Location("testLocationProvider");
        location.setLatitude(46.52890355757567);
        location.setLongitude(6.569420238493345);
        location.setAltitude(0);
        location.setTime(System.currentTimeMillis());

        TestInitUtils.initContext(location);

        firstPo = PhotoObjectTestUtils.germaynDeryckePO();
        firstPo.uploadWithoutFeedback();
        if(!onlyOnePhoto) {
            secondPo = PhotoObjectTestUtils.paulVanDykPO();
            secondPo.uploadWithoutFeedback();

        }
        Thread.sleep(3000);
        //final Object lock1 = new Object();
        //final Object lock2 = new Object();
        //synchronized (lock1)
        //{lock1.wait();}
        //synchronized (lock2)
        //{lock2.wait();}
        //LocalDatabase.getInstance().addPhotoObject(firstPo);
        //LocalDatabase.getInstance().addPhotoObject(secondPo);

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
