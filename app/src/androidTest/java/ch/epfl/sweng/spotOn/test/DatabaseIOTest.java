package ch.epfl.sweng.spotOn.test;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import java.util.ArrayList;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.media.PhotoObjectStoredInDatabase;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;

import static ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils.areEquals;
import static ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils.getAllPO;
import static ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils.getRandomPhotoObject;


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
            DatabaseRef.deletePhotoObjectFromDB(testObject1.getPictureId());
            StorageRef.deletePictureFromStorage(testObject1.getPictureId());
            throw new AssertionError("Test made it this far - test succeeded !");
        }

    }

    @Test
    public void mediasAreSentAndReceivedCorrectly() throws InterruptedException {
        for(PhotoObject p : getAllPO()) {
            final PhotoObject po = p;
            final String poId = po.getPictureId();
            final Object lock = new Object();
            po.upload(true, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });
            synchronized (lock) {
                lock.wait();
            }
            final DatabaseReference dbref = DatabaseRef.getMediaDirectory();
            dbref.orderByChild("pictureId").equalTo(poId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot wantedNode = dataSnapshot.child(poId);
                    if (!wantedNode.exists()) {
                        throw new AssertionError("nothing in the database at this spot : " + wantedNode.toString());
                    }
                    PhotoObjectStoredInDatabase databaseRetrievedObject = wantedNode.getValue(PhotoObjectStoredInDatabase.class);
                    PhotoObject retrievedPo = databaseRetrievedObject.convertToPhotoObject();
                    if (!areEquals(po, retrievedPo)) {
                        throw new AssertionError("expected : \n" + po.toString() + "\nReceived : \n" + retrievedPo.toString());
                    }
                    else{
                        //delete the PO after the test from the firebaseDB
                        DatabaseRef.deletePhotoObjectFromDB(poId);
                        StorageRef.deletePictureFromStorage(poId);

                    }
                    synchronized (lock) {
                        lock.notify();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            synchronized (lock) {
                lock.wait();
            }
        }
    }

    @Test
    public void retrievingFullsizeImagesWorkCorrectly() throws InterruptedException {
        for(PhotoObject p : getAllPO()) {

            // UPLOAD PHOTOOBJECT
            final PhotoObject original = p;
            final String poId = original.getPictureId();
            final Object lock = new Object();
            original.upload(true, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });
            synchronized (lock) {
                lock.wait();
            }
            // RETRIEVE OBJECT FROM DATABASE
            final DatabaseReference dbref = DatabaseRef.getMediaDirectory();
            dbref.orderByChild("pictureId").equalTo(poId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot wantedNode = dataSnapshot.child(poId);
                    if (!wantedNode.exists()) {
                        throw new AssertionError("nothing in the database at this spot : " + wantedNode.toString());
                    }
                    PhotoObjectStoredInDatabase databaseRetrievedObject = wantedNode.getValue(PhotoObjectStoredInDatabase.class);
                    final PhotoObject retrieved = databaseRetrievedObject.convertToPhotoObject();
                    retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject = retrieved;
                    synchronized (lock) {
                        lock.notify();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            synchronized (lock) {
                lock.wait();
            }
            // RETRIEVE FULLSIZEIMAGE FROM FILESERVER
            Log.d("dbio_test", "retrieved \n" + retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject.toString());
            retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject.retrieveFullsizeImage(true, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            });
            synchronized (lock) {
                lock.wait();
            }
            // CHECK WE DID ACTUALLY RETRIEVE THE IMAGE
            if (!retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject.hasFullSizeImage()) {
                throw new AssertionError("retrieved object should have fullsizeimage :\n" +
                        retrievingFullsizeImagesWorkCorrectly_retrievedPhotoObject.toString());
            }
            else{
                DatabaseRef.deletePhotoObjectFromDB(poId);
                StorageRef.deletePictureFromStorage(poId);
            }
        }
    }

    @Test
    public void gettersWorkCorrectly() throws Exception     {
        String imageLink = "bad_link";
        String pictureId = "key1";
        String author = "author1";
        String photoName = "name1";
        long createdDate = 1;
        double latitude = 4.4;
        double longitude = 6.6;
        int upvotes = 9;
        int downvotes = 7;
        int reports = 1;
        ArrayList<String> upvotersList = new ArrayList<String>();
        upvotersList.add("truc");
        upvotersList.add("machine");
        ArrayList<String> downvotersList = new ArrayList<String>();
        ArrayList<String> reportersList = new ArrayList<String>();
        reportersList.add("user1");
        PhotoObject photo1 = new PhotoObject(imageLink, null, "key1", author, photoName, createdDate,
                latitude, longitude, upvotes, downvotes, reports, upvotersList, downvotersList,
                reportersList);
        if (photo1.getLongitude() != longitude) {
            throw new AssertionError("longitude wrongly get");
        }
        if (photo1.getLatitude() != latitude) {
            throw new AssertionError("latitude wrongly get");
        }
        if (photo1.getCreatedDate().getTime() != createdDate) {
            throw new AssertionError("created date wrongly get");
        }
        if (photo1.getExpireDate().getTime() <= createdDate ) {
            throw new AssertionError("expire date wrongly get");
        }
        if (photo1.getRadius()>PhotoObject.MAX_VIEW_RADIUS || photo1.getRadius()<PhotoObject.MIN_VIEW_RADIUS) {
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
        if (!(photo1.getUpvotes() == upvotes)) {
            throw new AssertionError("votes wrongly get");
        }
        if (!(photo1.getDownvotes() == downvotes)) {
            throw new AssertionError("votes wrongly get");
        }
        if (!(photo1.getReports() == reports)) {
            throw new AssertionError("reports wrongly get");
        }
        if (!(photo1.getUpvotersList().equals(upvotersList))) {
            throw new AssertionError("voters wrongly get");
        }
        if (!(photo1.getDownvotersList().equals(downvotersList))) {
            throw new AssertionError("voters wrongly get");
        }
        if (!(photo1.getReportersList().equals(reportersList))) {
            throw new AssertionError("reporters wrongly get");
        }
    }


}
