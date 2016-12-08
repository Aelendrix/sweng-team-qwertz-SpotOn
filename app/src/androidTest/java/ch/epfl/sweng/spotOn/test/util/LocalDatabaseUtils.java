package ch.epfl.sweng.spotOn.test.util;

import android.location.Location;

import com.google.firebase.database.DatabaseReference;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

/**
 * Created by olivi on 07.12.2016.
 *
 * Created this class to refactor some code for the tests when we initialize the local database
 * and erase the photo objects from it at the end of each tests
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
        firstPo.uploadWithoutFeedback();

        secondPo = PhotoObjectTestUtils.paulVanDykPO();
        secondPo.uploadWithoutFeedback();

        LocalDatabase.getInstance().notifyListeners();
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
            }
            if(secondPo!=null){
                DatabaseRef.deletePhotoObjectFromDB(secondPo.getPictureId());
            }
        }
    }
}