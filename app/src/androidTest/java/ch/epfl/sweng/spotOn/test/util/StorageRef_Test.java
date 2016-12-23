package ch.epfl.sweng.spotOn.test.util;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/** Singleton the provides file server references for test purpose
 * Created by quentin on 04.11.16.
 *
 */

public class StorageRef_Test {

    private final static String mStorageRefString = "Images_test";
    private final static StorageReference mStorageReference = FirebaseStorage.getInstance().getReference(mStorageRefString);


    // PUBLIC METHODS
    public static StorageReference getMediaDirectory(){
        return mStorageReference;
    }


    // CONSTRUCTOR FOR SINGLETON
    private StorageRef_Test(){
    }


}
