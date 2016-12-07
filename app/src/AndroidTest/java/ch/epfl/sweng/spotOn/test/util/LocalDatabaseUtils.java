package ch.epfl.sweng.spotOn.test.util;

import android.content.Intent;
import android.location.Location;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;

/**
 * Created by olivi on 07.12.2016.
 *
 * This class was created to refactor some methods that are always called when instantiating
 * the local database for the tests and deleting the elements from it after the tests are done
 */

public class LocalDatabaseUtils {

    public static PhotoObject firstPo;
    public static PhotoObject secondPo;

    public static void initLocalDatabase() throws InterruptedException {
        Location location = new Location("testLocationProvider");
        location.setLatitude(46.52890355757567);
        location.setLongitude(6.569420238493345);
        location.setAltitude(0);
        location.setTime(System.currentTimeMillis());

        TestInitUtils.initContext(location);

        firstPo = PhotoObjectTestUtils.germaynDeryckePO();
        LocalDatabase.getInstance().addPhotoObject(firstPo);

        secondPo = PhotoObjectTestUtils.paulVanDykPO();
        LocalDatabase.getInstance().addPhotoObject(secondPo);

        LocalDatabase.getInstance().notifyListeners();
    }

    public static void afterTests(){
        ConcreteLocationTracker.destroyInstance();
        if( ConcreteLocationTracker.instanceExists()){
            throw new AssertionError("CameraTest : concreteLocationTracker mock instance not deleted : "+ConcreteLocationTracker.getInstance().getLocation());
        }
        // stuff below seems pretty unimportant to me
        if(LocalDatabase.instanceExists()){
            LocalDatabase ldb = LocalDatabase.getInstance();
            if(firstPo!=null){
                ldb.removePhotoObject( firstPo.getPictureId());
            }
            if(secondPo!=null){
                ldb.removePhotoObject(secondPo.getPictureId());
            }
        }
    }
}
