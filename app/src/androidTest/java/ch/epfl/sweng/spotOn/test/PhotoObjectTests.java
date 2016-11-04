package ch.epfl.sweng.spotOn.test;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.media.PhotoObjectStoredInDatabase;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.test.util.TestPhotoObjectUtils;

import static ch.epfl.sweng.spotOn.test.util.TestPhotoObjectUtils.getAllPO;
import static ch.epfl.sweng.spotOn.test.util.TestPhotoObjectUtils.getRandomPhotoObject;

/**
 *  Created by quentin on 26.10.16.
 */
@RunWith(AndroidJUnit4.class)
public class PhotoObjectTests {

    @Test
    public void chainedVotesRegisterWell(){
        for(PhotoObject p : getAllPO()) {
            String sameAuthorId = "jeVotePlusieursFois";
            p.processVote(1, sameAuthorId);
            p.processVote(-1, sameAuthorId);
            p.processVote(1, sameAuthorId);
            p.processVote(-1, sameAuthorId);
            p.processVote(1, sameAuthorId);
            p.processVote(-1, sameAuthorId);
            if(p.getUpvotes()!=1){
                throw new AssertionError("votes should cancel out");
            }
            p.processVote(1, "wertqwert");
            p.processVote(1, "zertherth");
            p.processVote(1, "wergwasdf");
            if(p.getUpvotes()!=4){
                throw new AssertionError("votes should all register\n"+p.toString());
            }
            p.processVote(1, sameAuthorId); // to remove the downvote this user issued during the 1st sequence of votes
            p.processVote(-1, "wefv<asdfv");
            p.processVote(-1, "zertheAFQ3RQrth");
            p.processVote(-1, "wergwasWQRFQWdf");
            if(p.getDownvotes()!=4){
                throw new AssertionError("votes should all registern\n"+p.toString());
            }
        }
    }

    @Test
    public void cantVoteTwice(){
        for(PhotoObject p : getAllPO()) {
            String sameAuthorId = "jeVotePlusieursFois";
            p.processVote(1, sameAuthorId);
            p.processVote(1, sameAuthorId);
            if(p.getUpvotes()!=2){
                throw new AssertionError("voted several times !\n"+p.toString());
            }
            p.processVote(-1, sameAuthorId);
            p.processVote(-1, sameAuthorId);
            if(p.getDownvotes()!=2){
                throw new AssertionError("voted several times !\n"+p.toString());
            }
        }
    }

    @Test
    public void upvoteOwnPictures(){
        for(PhotoObject p : getAllPO()) {
            p.processVote(1, p.getAuthorId());
            p.processVote(-1, p.getAuthorId());
            p.processVote(-1, p.getAuthorId());
            if (p.getUpvotes() != 1) {
                throw new AssertionError("You shouldn't be able to vote for your own pictures");
            }
            if (p.getDownvotes() != 1) {
                throw new AssertionError("You shouldn't be able to vote for your own pictures");
            }
        }
    }

    @Test (expected=IllegalArgumentException.class)
    public void invalidUpvoteValue(){
        PhotoObject p = getRandomPhotoObject();
        p.processVote(2, "user9981204598263");
    }

    @Test
    public void radiusForUpvotedMedia() throws InterruptedException {
        for(PhotoObject p : getAllPO()) {
            if (p.getRadius() != PhotoObject.DEFAULT_VIEW_RADIUS) {
                throw new AssertionError("at creation, should have default radius");
            }
            p.processVote(1, "user9981204598263");
            p.processVote(1, "user99ertz4598263");

            if (p.getRadius() <= PhotoObject.DEFAULT_VIEW_RADIUS) {
                throw new AssertionError();
            }
        }
    }

    @Test
    public void radiusForDownvotedMedia() throws InterruptedException {
        for(PhotoObject p : getAllPO()) {
            if (p.getRadius() != PhotoObject.DEFAULT_VIEW_RADIUS) {
                throw new AssertionError("at creation, should have default radius");
            }
            p.processVote(-1, "user9981204598263");
            p.processVote(-1, "user99ertz4598263");

            if (p.getRadius() >= PhotoObject.DEFAULT_VIEW_RADIUS) {
                throw new AssertionError();
            }
        }
    }















    @Test
    public void photoOBjectInstantiatesCorrectly(){
        Bitmap fullSizePic = null;
        fullSizePic = TestPhotoObjectUtils.getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/4/4e/Ice_Diving_2.jpg");
        String authorID = "author";
        String photoName = "photoName";
        Timestamp createdDate = new Timestamp(9001);
        double latitude = 10;
        double longitude = -10;
        assertCreatedObjectMatchesInitializationFields(fullSizePic, authorID, photoName, createdDate, latitude, longitude);
    }

// PRIVATE HELPERS

    private void assertCreatedObjectMatchesInitializationFields(Bitmap fullSizePic, String authorID, String photoName,
                                                               Timestamp createdDate, double latitude, double longitude){
        PhotoObject po = new PhotoObject(fullSizePic, authorID, photoName,
                createdDate, latitude, longitude);
        DatabaseReference DBref = FirebaseDatabase.getInstance().getReference("MediaPath");
        assert (po.getFullSizeImage() == fullSizePic);
        assert (po.getAuthorId() == authorID);
        assert (po.getPhotoName() == photoName);
        assert (po.getCreatedDate() == createdDate);
        assert (po.getExpireDate().getTime() > createdDate.getTime());
        assert (po.getLatitude() == latitude);
        assert (po.getLongitude() == longitude);
        assert (po.getPictureId().length() == DBref.push().getKey().length());
    }



}
