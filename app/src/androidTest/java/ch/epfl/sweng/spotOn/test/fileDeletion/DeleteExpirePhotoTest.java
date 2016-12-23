package ch.epfl.sweng.spotOn.test.fileDeletion;

import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;

/**
 * Created by nico on 23.11.16.
 *
 */
@SuppressWarnings("unused")
@RunWith(AndroidJUnit4.class)
public class DeleteExpirePhotoTest {
    String pictureID;
    //add a very old picture, the background service will trigger during the testing (very bad test)
    @Test  (timeout=10000)
    public void addVeryOldPictureToDB(){
        PhotoObject po = PhotoObjectTestUtils.veryOldTimestampPicture();
        pictureID = po.getPictureId();
        po.uploadWithoutFeedback();
    }
}
