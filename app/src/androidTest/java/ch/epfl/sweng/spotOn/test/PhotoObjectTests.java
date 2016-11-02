package ch.epfl.sweng.spotOn.test;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectUtils;

/**
 *  Created by quentin on 26.10.16.
 */
@RunWith(AndroidJUnit4.class)
public class PhotoObjectTests {

    @Test
    public void photoOBjectInstantiatesCorrectly(){
        Bitmap fullSizePic = null;
        fullSizePic = PhotoObjectUtils.getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/4/4e/Ice_Diving_2.jpg");
        String authorID = "author";
        String photoName = "photoName";
        Timestamp createdDate = new Timestamp(9001);
        double latitude = 10;
        double longitude = -10;
        int radius = 1;
        assertCreatedObjectMatchesInitializationFields(fullSizePic, authorID, photoName, createdDate, latitude, longitude, radius);
    }



// PRIVATE HELPERS

    private void assertCreatedObjectMatchesInitializationFields(Bitmap fullSizePic, String authorID, String photoName,
                                                               Timestamp createdDate, double latitude, double longitude, int radius){
        PhotoObject po = new PhotoObject(fullSizePic, authorID, photoName,
                createdDate, latitude, longitude, radius);
        DatabaseReference DBref = FirebaseDatabase.getInstance().getReference("MediaPath");
        assert (po.getFullSizeImage() == fullSizePic);
        assert (po.getAuthorId() == authorID);
        assert (po.getPhotoName() == photoName);
        assert (po.getCreatedDate() == createdDate);
        assert (po.getExpireDate().getTime() > createdDate.getTime());
        assert (po.getLatitude() == latitude);
        assert (po.getLongitude() == longitude);
        assert (po.getRadius() == radius);
        assert (po.getPictureId().length() == DBref.push().getKey().length());
    }



}
