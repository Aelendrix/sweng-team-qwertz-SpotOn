package ch.epfl.sweng.spotOn.test;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DatabaseReference;


import junit.framework.Assert;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.Timestamp;

import java.util.ArrayList;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;

import static ch.epfl.sweng.spotOn.media.PhotoObject.DEFAULT_LIFETIME;
import static ch.epfl.sweng.spotOn.media.PhotoObject.DEFAULT_VIEW_RADIUS;
import static ch.epfl.sweng.spotOn.media.PhotoObject.MAX_LIFETIME;
import static ch.epfl.sweng.spotOn.media.PhotoObject.MAX_VIEW_RADIUS;
import static ch.epfl.sweng.spotOn.media.PhotoObject.MIN_LIFETIME;
import static ch.epfl.sweng.spotOn.media.PhotoObject.MIN_VIEW_RADIUS;
import static ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils.getAllPO;
import static ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils.getRandomPhotoObject;

/** Tests all aspects of PhotoObjects (upload / download / changing radius / changing lifetime / votes / ...)
 *  Created by quentin on 26.10.16.
 */
@RunWith(AndroidJUnit4.class)
public class PhotoObjectTests {

    @Test
    public void upvotesChangeLifetime(){
        for(PhotoObject p : getAllPO()) {
            long originalLifetime = p.getExpireDate().getTime()-p.getCreatedDate().getTime();
            if(originalLifetime!=DEFAULT_LIFETIME){
                throw new AssertionError("initially, lifetime should be default");
            }
            p.processVote(1, "wertqwert");
            p.processVote(1, "zertherth");
            p.processVote(1, "wergwasdf");
            double newPopularityRatio = (double)(4-1)/(double)5;
            long newLifetime = p.getExpireDate().getTime()-p.getCreatedDate().getTime();
            if(newLifetime != (int)Math.ceil(DEFAULT_LIFETIME + newPopularityRatio*(MAX_LIFETIME-DEFAULT_LIFETIME)) || newLifetime<originalLifetime){
                throw new AssertionError("new lifetime is wrong");
            }
        }
    }

    @Test
    public void downvotesChangeLifetime(){
        for(PhotoObject p : getAllPO()) {
            long originalLifetime = p.getExpireDate().getTime()-p.getCreatedDate().getTime();
            if(originalLifetime!=DEFAULT_LIFETIME){
                throw new AssertionError("initially, lifetime should be default");
            }
            p.processVote(-1, "wertqwert");
            p.processVote(-1, "zertherth");
            p.processVote(-1, "wergwasdf");
            double newPopularityRatio = (double)(1-4)/(double)5;
            double newUnPopularityRatio = -newPopularityRatio;
            long expectedLifetime  = (int)Math.ceil(MIN_LIFETIME + newUnPopularityRatio*(DEFAULT_LIFETIME-MIN_LIFETIME));
            long newLifetime = p.getExpireDate().getTime()-p.getCreatedDate().getTime();
            if(newLifetime != expectedLifetime || newLifetime>originalLifetime){
                throw new AssertionError("new lifetime is wrong expected : "+expectedLifetime+" computed : "+newLifetime);
            }
        }
    }

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
                throw new AssertionError("votes should all be registered\n"+p.toString());
            }
            p.processVote(1, sameAuthorId); // to remove the downvote this user issued during the 1st sequence of votes
            p.processVote(-1, "wefv<asdfv");
            p.processVote(-1, "zertheAFQ3RQrth");
            p.processVote(-1, "wergwasWQRFQWdf");
            if(p.getDownvotes()!=4){
                throw new AssertionError("votes should all be registered\n"+p.toString());
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
            if (p.getRadius() != DEFAULT_VIEW_RADIUS) {
                throw new AssertionError("at creation, should have default radius");
            }
            p.processVote(1, "user9981204598263");
            p.processVote(1, "user99ertz4598263");

            double newPopularityRatio = ((double)(3-1))/((double)4);
            int expectedRadius = (int)Math.ceil(DEFAULT_VIEW_RADIUS + newPopularityRatio*(MAX_VIEW_RADIUS-DEFAULT_VIEW_RADIUS));
            if (p.getRadius()!= expectedRadius || p.getRadius() <= DEFAULT_VIEW_RADIUS) {
                throw new AssertionError("new radius incorrect : expected="+expectedRadius+" computed="+p.getRadius());
            }
        }
    }

    @Test
    public void radiusForDownvotedMedia() throws InterruptedException {
        for(PhotoObject p : getAllPO()) {
            if (p.getRadius() != DEFAULT_VIEW_RADIUS) {
                throw new AssertionError("at creation, should have default radius");
            }
            p.processVote(-1, "user9981204598263");
            p.processVote(-1, "user99ertz4598263");


            double newPopularityRatio = ((double)(1-3))/((double)4);
            int expectedRadius = (int)Math.ceil(MIN_VIEW_RADIUS + (-1)*newPopularityRatio*(DEFAULT_VIEW_RADIUS-MIN_VIEW_RADIUS));

            if (p.getRadius()!= expectedRadius || p.getRadius() >= DEFAULT_VIEW_RADIUS) {
                throw new AssertionError("new radius incorrect : expected="+expectedRadius+" computed="+p.getRadius());
            }
        }
    }

    /* Test not working :(
    @Test
    public void retrieveImageException() throws Exception{
        PhotoObjectStoredInDatabase po1 = new PhotoObjectStoredInDatabase(null, null, null, "1", "xD",
                new Timestamp(0), new Timestamp(1), 0, 0, 1, 4, new ArrayList<String>(), new ArrayList<String>());
        PhotoObject pObject1 = po1.convertToPhotoObject();
        PhotoObjectStoredInDatabase po2 = new PhotoObjectStoredInDatabase("xD", null, null, "1", "xD",
                new Timestamp(0), new Timestamp(1), 0, 0, 1, 4, new ArrayList<String>(), new ArrayList<String>());
        PhotoObject pObject2 = po1.convertToPhotoObject();
        try{
            pObject1.retrieveFullsizeImage(true,null);
        }
        catch(AssertionError e) {
        }

        try{
            pObject2.retrieveFullsizeImage(true,null);
        }
        catch(NullPointerException e) {
        }
    }
    */

    @Test
    public void testProcessReport(){
        for(PhotoObject p : getAllPO()) {
            ArrayList<String> reporters = new ArrayList<String>(){};
            reporters.add("user1");

            p.processReport("user1");

            Assert.assertEquals(p.getReports(), 1);
            Assert.assertEquals(p.getReportersList().equals(reporters), true);
        }
    }

    @Test
    public void photoObjectInstantiatesCorrectly(){
        Bitmap fullSizePic = null;
        fullSizePic = PhotoObjectTestUtils.getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/4/4e/Ice_Diving_2.jpg");
        String authorID = "author";
        String photoName = "photoName";
        Timestamp createdDate = new Timestamp(9001);
        double latitude = 10;
        double longitude = -10;
        assertCreatedObjectMatchesInitializationFields(fullSizePic, authorID, photoName, createdDate, latitude, longitude);
    }


    @Test
    public void toStringIsCorrect(){
        PhotoObject p = PhotoObjectTestUtils.getRandomPhotoObject();
        if(!p.toString().contains(p.getPictureId())){
            throw new AssertionError(" toString() should at least contain the pictureId");
        }
        if(!PhotoObjectTestUtils.convertToStoredInDatabase(p).toString().contains(p.getPictureId())){
            throw new AssertionError(" toString() should at least contain the pictureId after conversion");
        }
    }


// PRIVATE HELPERS

    private void assertCreatedObjectMatchesInitializationFields(Bitmap fullSizePic, String authorID, String photoName,
                                                               Timestamp createdDate, double latitude, double longitude){
        PhotoObject po = new PhotoObject(fullSizePic, authorID, photoName,
                createdDate, latitude, longitude);
        DatabaseReference DBref = DatabaseRef.getMediaDirectory();

        Assert.assertEquals(po.getFullSizeImage() == fullSizePic,true);
        Assert.assertEquals(po.getAuthorId().equals(authorID), true);
        Assert.assertEquals(po.getPhotoName().equals(photoName), true);
        Assert.assertEquals(po.getCreatedDate(), createdDate);
        Assert.assertEquals(po.getExpireDate().getTime() > createdDate.getTime(), true);
        Assert.assertEquals(po.getLatitude(), latitude);
        Assert.assertEquals(po.getLongitude(), longitude);
        Assert.assertEquals(po.getPictureId().length(), DBref.push().getKey().length());
    }
}
