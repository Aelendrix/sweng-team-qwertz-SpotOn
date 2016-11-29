package ch.epfl.sweng.spotOn.test;

import android.graphics.Bitmap;
import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.MockLocationTracker_forTest;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;
import ch.epfl.sweng.spotOn.test.util.TestInitUtils;

/**
 * Created by nico on 27.10.16.
 */
@RunWith(AndroidJUnit4.class)
public class LocalDatabaseTest {

    PhotoObject photo1 = PhotoObjectTestUtils.paulVanDykPO();
    PhotoObject photo2 = PhotoObjectTestUtils.iceDivingPO();
    PhotoObject photo3 = PhotoObjectTestUtils.germaynDeryckePO();


    @Before
    public void init(){
        // don't change the MockLocationTracker_forTest latitude and longitude
//        MockLocationTracker_forTest mlt = new MockLocationTracker_forTest(46.52890355757567, 6.569420238493345);
//        LocalDatabase.initialize(mlt);
        Location l = new Location("mockProvider_LocalDatabaseTest");
        l.setLatitude(46.52890355757567);
        l.setLongitude(6.569420238493345);
        TestInitUtils.initContext(l);
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

        LocalDatabase.getInstance().addPhotoObject(photo1);
        if(!PhotoObjectTestUtils.areEquals(photo1,LocalDatabase.getInstance().get(photo1.getPictureId()))) {
            throw new AssertionError("LocalDB give wrong photo");
        }
        LocalDatabase.getInstance().clear();
    }

    @Test
    public void getThumbnailListTest() throws Exception{
        LocalDatabase.getInstance().clear();
        LocalDatabase.getInstance().addPhotoObject(photo1);
        LocalDatabase.getInstance().addPhotoObject(photo2);
        LocalDatabase.getInstance().addPhotoObject(photo3);
        Map<String,Bitmap> thumbList = LocalDatabase.getInstance().getViewableThumbnails();

        if(thumbList.size()!=3){ // the 3 pictures are within range, if the MockLocation latitude and longitude aren't changed
            throw new AssertionError("return a list with a different size ("+thumbList.size()+") than expected (3)");
        }
        if( !thumbList.get(photo1.getPictureId()).sameAs(photo1.getThumbnail()) ||
            !thumbList.get(photo2.getPictureId()).sameAs(photo2.getThumbnail()) ||
            !thumbList.get(photo3.getPictureId()).sameAs(photo3.getThumbnail()) )
        {
            throw new AssertionError("incorrect database content");
        }
    }
}
