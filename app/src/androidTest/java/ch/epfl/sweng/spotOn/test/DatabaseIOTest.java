package ch.epfl.sweng.spotOn.test;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.NoSuchElementException;
import java.util.Objects;

import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.media.PhotoObjectStoredInDatabase;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectUtils;

import static ch.epfl.sweng.spotOn.test.util.PhotoObjectUtils.areEquals;
import static ch.epfl.sweng.spotOn.test.util.PhotoObjectUtils.getRandomPhotoObject;

/** test the database behavious with
 *  @author quentin
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseIOTest {

    private final Object lock = new Object();
    private boolean tester = false;

    @Test
    public void runTests() throws Exception {
        objectIsSendAndReceivedCorrectly(getRandomPhotoObject());
    }

    public void objectIsSendAndReceivedCorrectly(final PhotoObject testOBject1) throws Exception {
        final String testObjectId = testOBject1.getPictureId();

        final DatabaseIOTest refToLock = this;

        testOBject1.upload(true, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                tester=true;
                synchronized(refToLock) {
                    refToLock.notify();
                }
            }
        });

        synchronized (this) {
            this.wait();
        }



        throw new AssertionError("completed");
    }
    /*
        DBref.child(testObjectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    throw new NoSuchElementException("datasnapshot \""+DBref+dataSnapshot.getKey()+"\" doesn't exist");
                }
                PhotoObject receivedPhotoOBject = dataSnapshot.getValue(PhotoObjectStoredInDatabase.class).convertToPhotoObject();
                if(!areEquals(receivedPhotoOBject, testOBject1)){
                    throw new AssertionError("the send and received objects are different \n"
                            +testOBject1.toString()+"\n"+receivedPhotoOBject.toString());
                }
                synchronized (lock) {
                    lock.notify();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw new Error();
            }
        });

        synchronized (lock) {
            lock.wait();
        }
    }

    /*
    @Test (expected=AssertionError.class)
    public void objectWithWrongPictureIsRejected() throws Exception {
        final PhotoObject testOBject1 = PhotoObjectUtils.paulVanDykPO();
        final String testObjectId = testOBject1.getPictureId();
        testOBject1.upload();

        final PhotoObject testOBject_wrong = PhotoObjectUtils.germaynDeryckePO();

        PhotoObjectUtils.DBref.child(testObjectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    throw new NoSuchElementException("datasnapshot doesn't exist");
                }
                PhotoObject receivedPhotoOBject = dataSnapshot.getValue(PhotoObjectStoredInDatabase.class).convertToPhotoObject();
                if(!areEquals(receivedPhotoOBject, testOBject_wrong)){
                    throw new AssertionError("testObjects are equal, should be different");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // nada
            }
        });

    }
    */

}
