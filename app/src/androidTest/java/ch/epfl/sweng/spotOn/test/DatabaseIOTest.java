package ch.epfl.sweng.spotOn.test;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.media.PhotoObjectStoredInDatabase;

import static ch.epfl.sweng.spotOn.test.util.TestPhotoObjectUtils.areEquals;
import static ch.epfl.sweng.spotOn.test.util.TestPhotoObjectUtils.getRandomPhotoObject;


/** test the behaviour of photoObjects when sent/received from database and fileserver
 *  @author quentin
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseIOTest {

    private boolean listenerExecuted_objectIsSentAndtestWaitsForSentCompleted;

    private PhotoObject retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject=null;

    @Test(expected=AssertionError.class)
    public void objectIsSentAndtestWaitsForSentCompleted() throws AssertionError, InterruptedException {
        PhotoObject testObject1 = getRandomPhotoObject();

        final Object lock = new Object();
        listenerExecuted_objectIsSentAndtestWaitsForSentCompleted=false;

        testObject1.upload(true, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listenerExecuted_objectIsSentAndtestWaitsForSentCompleted=true;
                synchronized(lock) {
                    lock.notify();
                }
            }
        });

        synchronized (lock) {
            lock.wait();
        }

        if(listenerExecuted_objectIsSentAndtestWaitsForSentCompleted) {
            throw new AssertionError("Test made it this far - test succeeded !");
        }
    }

    @Test
    public void mediasAreSentAndReceivedCorrectly() throws InterruptedException {
        final PhotoObject po = getRandomPhotoObject();
        final String poId = po.getPictureId();
        final Object lock = new Object();
        po.upload(true, new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                synchronized(lock) {
                    lock.notify();
                }
            }
        });
        synchronized (lock){
            lock.wait();
        }
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("MediaDirectory");
        dbref.orderByChild("pictureId").equalTo(poId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot wantedNode = dataSnapshot.child(poId);
                if(!wantedNode.exists()){
                    throw new AssertionError("nothing in the database at this spot : "+wantedNode.toString());
                }
                PhotoObjectStoredInDatabase databaseRetrievedObject = wantedNode.getValue(PhotoObjectStoredInDatabase.class);
                PhotoObject retrievedPo = databaseRetrievedObject.convertToPhotoObject();
                if(!areEquals(po, retrievedPo)){
                    throw new AssertionError("expected : \n"+po.toString()+"\nReceived : \n"+retrievedPo.toString());
                }
                synchronized (lock){
                    lock.notify();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        synchronized (lock){
            lock.wait();
        }
    }

    @Test
    public void retrievingFullsizeImagesWorkCorrectly() throws InterruptedException {
        // UPLOAD A RANDOM PHOTOOBJECT
        final PhotoObject original = getRandomPhotoObject();
        final String poId = original.getPictureId();
        final Object lock = new Object();
        original.upload(true, new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                synchronized(lock) {
                    lock.notify();
                }
            }
        });
        synchronized (lock){
            lock.wait();
        }
        // OBTAIN OBJECT FROM DATABASE
        final DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("MediaDirectory");
        dbref.orderByChild("pictureId").equalTo(poId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot wantedNode = dataSnapshot.child(poId);
                if(!wantedNode.exists()){
                    throw new AssertionError("nothing in the database at this spot : "+wantedNode.toString());
                }
                PhotoObjectStoredInDatabase databaseRetrievedObject = wantedNode.getValue(PhotoObjectStoredInDatabase.class);
                final PhotoObject retrieved = databaseRetrievedObject.convertToPhotoObject();
                retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject = retrieved;
                synchronized (lock){
                    lock.notify();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        synchronized (lock){
            lock.wait();
        }
        // RETRIEVE FULLSIZEIMAGE FROM FILESERVER
        Log.d("dbio_test","retrieved \n"+retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject.toString());
        retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject.retrieveFullsizeImage(true, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                synchronized (lock){
                    lock.notify();
                }
            }
        });
        synchronized (lock){
            lock.wait();
        }
        // CHECK WE DID RETRIEVE THE IMAGE
        if(!retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject.hasFullSizeImage()){
            throw new AssertionError("retrieved object should have fullsizeimage :\n"+
                    retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject.toString());
        }
    }

    @Test
    public void gettersWorkCorrectly() throws Exception {
        String imageLink = "bad_link";
        String pictureId = "key1";
        String author = "author1";
        String photoName = "name1";
        long createdDate = 1;
        long expireDate = 1000;
        double latitude = 4.4;
        double longitude = 6.6;
        int radius = 99;
        int votes = 0;
        List<String> voters = new ArrayList<String>();
        voters.add(author);
        PhotoObject photo1 = new PhotoObject(imageLink, null, "key1", author, photoName, createdDate,
                expireDate, latitude, longitude, radius, votes, voters);
        if (photo1.getLongitude() != longitude) {
            throw new AssertionError("longitude wrongly get");
        }
        if (photo1.getLatitude() != latitude) {
            throw new AssertionError("latitude wrongly get");
        }
        if (photo1.getCreatedDate().getTime() != createdDate) {
            throw new AssertionError("created date wrongly get");
        }
        if (photo1.getExpireDate().getTime() != expireDate) {
            throw new AssertionError("expire date wrongly get");
        }
        if (photo1.getRadius() != radius) {
            throw new AssertionError("radius wrongly get");
        }
        if (!photo1.getAuthorId().equals(author)) {
            throw new AssertionError("author wrongly get");
        }
        if (!photo1.getPhotoName().equals(photoName)) {
            throw new AssertionError("photo name wrongly get");
        }
        if (!photo1.getFullsizeImageLink().equals(imageLink)) {
            throw new AssertionError("image link wrongly get");
        }
        if (!photo1.getPictureId().equals(pictureId)) {
            throw new AssertionError("image id wrongly get");
        }
        if (!(photo1.getVotes() == votes)) {
            throw new AssertionError("votes wrongly get");
        }
        if (!(photo1.getVoters().equals(voters))) {
            throw new AssertionError("voters wrongly get");
        }

    }


}
