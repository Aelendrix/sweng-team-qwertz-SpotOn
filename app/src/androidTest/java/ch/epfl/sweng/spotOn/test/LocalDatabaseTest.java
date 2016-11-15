package ch.epfl.sweng.spotOn.test;

import android.graphics.Bitmap;
import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.TestPhotoObjectUtils;

/**
 * Created by nico on 27.10.16.
 */
@RunWith(AndroidJUnit4.class)
public class LocalDatabaseTest {

    PhotoObject photo1 = TestPhotoObjectUtils.paulVanDykPO();
    PhotoObject photo2 = TestPhotoObjectUtils.iceDivingPO();
    PhotoObject photo3 = TestPhotoObjectUtils.germaynDeryckePO();
    Location location = new Location("");


    @Test
    public void addToDB() throws Exception {
        LocalDatabase.addPhotoObject(photo1);
        if(!LocalDatabase.hasKey(photo1.getPictureId()))
        throw new AssertionError("Picture not added on the localDB");
    }

    @Test
    public void removeToDB() throws Exception {
        if(LocalDatabase.hasKey(photo1.getPictureId())) {
            LocalDatabase.deletePhotoObject(photo1.getPictureId());
        }
        if(!LocalDatabase.hasKey(photo1.getPictureId())) {
            LocalDatabase.addPhotoObject(photo1);
            LocalDatabase.deletePhotoObject(photo1.getPictureId());
        }
        if(LocalDatabase.hasKey(photo1.getPictureId())) {
            throw new AssertionError("Picture not removed from the localDB");
        }

    }

    @Test
    public void getPhotoFromDB() throws Exception{
        if(LocalDatabase.hasKey(photo1.getPictureId()))
        {
            LocalDatabase.deletePhotoObject(photo1.getPictureId());
        }
        LocalDatabase.addPhotoObject(photo1);
        if(!TestPhotoObjectUtils.areEquals(photo1,LocalDatabase.getPhoto(photo1.getPictureId()))) {
            throw new AssertionError("LocalDB give wrong photo");
        }
    }

    @Test
    public void locationTest() throws Exception{
        LocalDatabase.setLocation(location);
        if(!LocalDatabase.getLocation().equals(location))
        {
            throw new AssertionError("Location not correctly set");
        }
    }

    @Test
    public void getThumbnailListTest() throws Exception{
        LocalDatabase.clearData();
        LocalDatabase.addPhotoObject(photo1);
        LocalDatabase.addPhotoObject(photo2);
        LocalDatabase.addPhotoObject(photo3);
        Map<String,Bitmap> thumbList = LocalDatabase.getViewableThumbnail();
        /*
        if(thumbList.size()!=3)
        {
            throw new AssertionError("return a list with a different size than the map");
        }
        //TODO: finish this test, i blame @sg.pepper to use a list of pair instead of map
        //if(thumbList.
        */
    }
}
