package ch.epfl.sweng.spotOn.test;

import android.graphics.Bitmap;
import android.location.Location;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectUtils;

/**
 * Created by nico on 27.10.16.
 */
@RunWith(AndroidJUnit4.class)
public class LocalDatabaseTest {

    PhotoObject photo1 = new PhotoObject("invalid link", null, "key1", "author1", "name1", 0, 10000, 0, 0, 0);
    PhotoObject photo2 = new PhotoObject("invalid link", null, "key2", "author2", "name2", 0, 10000, 0, 0, 0);
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
}
