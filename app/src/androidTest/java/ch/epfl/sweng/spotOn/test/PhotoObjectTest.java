package ch.epfl.sweng.spotOn.test;

import android.graphics.Bitmap;

import org.junit.Test;

import java.sql.Timestamp;

import ch.epfl.sweng.spotOn.media.PhotoObject;

/**
 * Created by Bruno on 27/10/2016.
 */

public class PhotoObjectTest {

    private Bitmap bitmap = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_4444);//create an arbitrary bitmap for now
    private PhotoObject mPhoto = new PhotoObject(bitmap, "SecretAuthor", "SecretPhoto",
            new Timestamp(System.currentTimeMillis()), 42.42, 45.45, 100);

    @Test
    public void upvoteAddVote(){
        assert(mPhoto.getVotes() == 0);
        mPhoto.upvote();
        mPhoto.upvote();
        assert(mPhoto.getVotes() == 2);
    }

    @Test
    public void downvoteDecreaseVote(){
        mPhoto.downvote();
        mPhoto.downvote();
        assert(mPhoto.getVotes() == 0);
        mPhoto.downvote();
        assert(mPhoto.getVotes() == -1);
    }
}

/*PhotoObject(Bitmap fullSizePic, String authorID, String photoName,
                       Timestamp createdDate, double latitude, double longitude, int radius)*/
