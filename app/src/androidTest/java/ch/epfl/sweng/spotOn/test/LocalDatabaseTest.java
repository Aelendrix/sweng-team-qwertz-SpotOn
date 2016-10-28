package ch.epfl.sweng.spotOn.test;

import android.graphics.Bitmap;
import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectUtils;
import ch.epfl.sweng.spotOn.util.Pair;

/**
 * Created by nico on 27.10.16.
 */
@RunWith(AndroidJUnit4.class)
public class LocalDatabaseTest {

    PhotoObject photo1 = PhotoObjectUtils.paulVanDykPO();
    PhotoObject photo2 = PhotoObjectUtils.iceDivingPO();
    PhotoObject photo3 = PhotoObjectUtils.germaynDeryckePO();
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
            LocalDatabase.deletePhotoObject(photo1);
        }
        if(!LocalDatabase.hasKey(photo1.getPictureId())) {
            LocalDatabase.addPhotoObject(photo1);
            LocalDatabase.deletePhotoObject(photo1);
        }
        if(LocalDatabase.hasKey(photo1.getPictureId())) {
            throw new AssertionError("Picture not removed from the localDB");
        }

    }

    @Test
    public void getPhotoFromDB() throws Exception{
        if(LocalDatabase.hasKey(photo1.getPictureId()))
        {
            LocalDatabase.deletePhotoObject(photo1);
        }
        LocalDatabase.addPhotoObject(photo1);
        if(!PhotoObjectUtils.areEquals(photo1,LocalDatabase.getPhoto(photo1.getPictureId()))) {
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
        List<Pair<Bitmap,String>> thumbList = LocalDatabase.getThumbnailArray();
        if(thumbList.size()!=3)
        {
            throw new AssertionError("return a list with a different size than the map");
        }
        //TODO: finish this test, i blame @sg.pepper to use a list of pair instead of map
        //if(thumbList.
    }


}
