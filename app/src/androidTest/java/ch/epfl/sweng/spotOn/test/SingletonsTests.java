package ch.epfl.sweng.spotOn.test;

import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.test.util.DatabaseRef_Test;
import ch.epfl.sweng.spotOn.test.util.StorageRef_Test;

/**
 * Created by quentin on 04.11.16.
 */

@RunWith(AndroidJUnit4.class)
public class SingletonsTests {

    @Test
    public void testDatabaseRefReferences(){
        if(!(DatabaseRef.getUsersDirectory().equals(FirebaseDatabase.getInstance().getReference("UsersDirectory")))){
            throw new AssertionError();
        }
        if(!(DatabaseRef.getMediaDirectory().equals(FirebaseDatabase.getInstance().getReference("MediaDirectory_v2")))){
            throw new AssertionError();
        }
        if(!(DatabaseRef.getRootDirectory().equals(FirebaseDatabase.getInstance().getReference()))){
            throw new AssertionError();
        }


        // these classes should probably get deleted soon
        if(!(DatabaseRef_Test.getUsersDirectory().equals(FirebaseDatabase.getInstance().getReference("UsersDirectory_test")))){
            throw new AssertionError();
        }
        if(!(DatabaseRef_Test.getMediaDirectory().equals(FirebaseDatabase.getInstance().getReference("MediaDirectory_test")))){
            throw new AssertionError();
        }
        if(!(DatabaseRef_Test.getMediaDirectory().equals(FirebaseDatabase.getInstance().getReference("MediaDirectory_test")))){
            throw new AssertionError();
        }
    }

    @Test
    public void testDBIllegalArgumentDelete() throws IllegalArgumentException{
        try{
            DatabaseRef.deletePhotoObjectFromDB(null);
            throw new AssertionError("IllegalArgumentException not detected");
        }
        catch(IllegalArgumentException e){

        }
    }
    @Test
    public void testStorageIllegalArgumentDelete() throws IllegalArgumentException{
        try{
            StorageRef.deletePictureFromStorage(null);
            throw new AssertionError("IllegalArgumentException not detected");
        }
        catch(IllegalArgumentException e){

        }
    }
    @Test
    public void testStorageReferences(){
        if(!(StorageRef.getMediaDirectory().equals(FirebaseStorage.getInstance().getReference("Images")))){
            throw new AssertionError();
        }
        if(!(StorageRef_Test.getMediaDirectory().equals(FirebaseStorage.getInstance().getReference("Images_test")))){
            throw new AssertionError();
        }
    }


}
