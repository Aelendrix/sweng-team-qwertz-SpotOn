package ch.epfl.sweng.spotOn.test;

import android.graphics.Bitmap;
import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker;
import ch.epfl.sweng.spotOn.test.util.TestPhotoObjectUtils;

/**
 * Created by nico on 27.10.16.
 */
@RunWith(AndroidJUnit4.class)
public class LocalDatabaseTest {

    PhotoObject photo1 = TestPhotoObjectUtils.paulVanDykPO();
    PhotoObject photo2 = TestPhotoObjectUtils.iceDivingPO();
    PhotoObject photo3 = TestPhotoObjectUtils.germaynDeryckePO();


    @Before
    public void init(){
        // don't change the MockLocationTracker latitude and longitude
        MockLocationTracker mlt = new MockLocationTracker(46.52890355757567, 6.569420238493345);
        LocalDatabase.initialize(mlt);
    }

    @Test
    public void addToDB() throws Exception {
        LocalDatabase.getInstance().clear();
        LocalDatabase.getInstance().addPhotoObject(photo1);
        if(!LocalDatabase.getInstance().hasKey(photo1.getPictureId())){
            throw new AssertionError("Picture not added on the localDB");
        }
        LocalDatabase.getInstance().clear();
    }

    @Test
    public void removeToDB() throws Exception {
        LocalDatabase.getInstance().clear();
        if(LocalDatabase.getInstance().hasKey(photo1.getPictureId())) {
            LocalDatabase.getInstance().removePhotoObject(photo1.getPictureId());
        }
        if(!LocalDatabase.getInstance().hasKey(photo1.getPictureId())) {
            LocalDatabase.getInstance().addPhotoObject(photo1);
            LocalDatabase.getInstance().removePhotoObject(photo1.getPictureId());
        }
        if(LocalDatabase.getInstance().hasKey(photo1.getPictureId())) {
            throw new AssertionError("Picture not removed from the localDB");
        }
        LocalDatabase.getInstance().clear();
    }

    @Test
    public void getPhotoFromDB() throws Exception{
        LocalDatabase.getInstance().clear();
        if(LocalDatabase.getInstance().hasKey(photo1.getPictureId())){
            LocalDatabase.getInstance().removePhotoObject(photo1.getPictureId());
        }
        LocalDatabase.getInstance().addPhotoObject(photo1);
        if(!TestPhotoObjectUtils.areEquals(photo1,LocalDatabase.getInstance().get(photo1.getPictureId()))) {
            throw new AssertionError("LocalDB give wrong photo");
        }
        LocalDatabase.getInstance().clear();
    }

//    /* tested method no longer exists
//    @Test
//    public void locationTest() throws Exception{
//        LocalDatabase.setLocation(location);
//        if(!LocalDatabase.getLocation().equals(location))
//        {
//            throw new AssertionError("Location not correctly set");
//        }
//    }*/

    @Test
    public void getThumbnailListTest() throws Exception{
        LocalDatabase.getInstance().clear();
        LocalDatabase.getInstance().addPhotoObject(photo1);
        LocalDatabase.getInstance().addPhotoObject(photo2);
        LocalDatabase.getInstance().addPhotoObject(photo3);
        Map<String,Bitmap> thumbList = LocalDatabase.getInstance().getViewableThumbmails();

        if(thumbList.size()!=3){ // the 3 pictures are within range, if the MockLocation latitude and longitude aren't changed
            throw new AssertionError("return a list with a different size ("+thumbList.size()+") than the map");
        }
        if( !thumbList.get(photo1.getPictureId()).sameAs(photo1.getThumbnail()) ||
            !thumbList.get(photo2.getPictureId()).sameAs(photo2.getThumbnail()) ||
            !thumbList.get(photo3.getPictureId()).sameAs(photo3.getThumbnail()) )
        {
            throw new AssertionError("incorrect database content");
        }
    }
}
