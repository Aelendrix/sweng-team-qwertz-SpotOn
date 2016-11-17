package ch.epfl.sweng.spotOn.singletonReferences;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    public static void deletePictureFromStorage(String pictureID){
        if(pictureID == null)
        {
            Log.e("Error in StorageRef","deletePictureFromStorage pictureId is null");
        }
        else {
            mMediaDirectory.child(pictureID + ".jpg").delete();
        }
    }

// CONSTUCTOR FOR SINGLETON
    private StorageRef(){
        // empty
    }


}
