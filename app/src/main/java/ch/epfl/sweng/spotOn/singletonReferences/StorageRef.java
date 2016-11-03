package ch.epfl.sweng.spotOn.singletonReferences;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/** Singleton the provides access
 * Created by quentin on 04.11.16.
 */

public class StorageRef {

    private final String mStorageRefString = "images";;
    private final StorageReference mStorageReference = FirebaseStorage.getInstance().getReference(mStorageRefString);


// PUBLIC METHODS
    public StorageReference getStorageReference(){
        return mStorageReference;
    }


// CONSTUCTOR FOR SINGLETON
    private StorageRef(){
        // empty
    }


}
