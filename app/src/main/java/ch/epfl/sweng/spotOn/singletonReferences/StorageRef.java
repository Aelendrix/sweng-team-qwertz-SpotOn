package ch.epfl.sweng.spotOn.singletonReferences;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/** Singleton the provides references to file server
 * Created by quentin on 04.11.16.
 */

public class StorageRef {

    private final static String mMediaDirectoryString = "Images";
    private final static StorageReference mMediaDirectory = FirebaseStorage.getInstance().getReference(mMediaDirectoryString);

    // PUBLIC METHODS
    public static StorageReference getMediaDirectory(){
        return mMediaDirectory;
    }

    /**
     * delete a the picture file from the storage depending of his pictureID
     * @param pictureID a unique ID linked to a picture in the firebase DB
     */
    public static void deletePictureFromStorage(String pictureID){
        if(pictureID == null)
        {
            throw new IllegalArgumentException("Error in StorageRef: deletePictureFromStorage, pictureId is null");
        }
        else {
            mMediaDirectory.child(pictureID + ".jpg").delete();
        }
    }

    // CONSTRUCTOR FOR SINGLETON
    private StorageRef(){
    }
}
