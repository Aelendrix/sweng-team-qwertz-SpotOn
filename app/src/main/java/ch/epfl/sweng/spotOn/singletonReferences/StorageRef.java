package ch.epfl.sweng.spotOn.singletonReferences;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/** Singleton the provides references to file server
 * Created by quentin on 04.11.16.
 */

public class StorageRef {

    private final static String mMediaDirectoryString = "images";;
    private final static StorageReference mMediaDirectory = FirebaseStorage.getInstance().getReference(mMediaDirectoryString);


// PUBLIC METHODS
    public static StorageReference getMediaDirectory(){
        return mMediaDirectory;
    }


// CONSTUCTOR FOR SINGLETON
    private StorageRef(){
        // empty
    }


}
