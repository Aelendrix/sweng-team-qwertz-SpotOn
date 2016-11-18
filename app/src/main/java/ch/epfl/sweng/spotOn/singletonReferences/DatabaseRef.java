package ch.epfl.sweng.spotOn.singletonReferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.BuildConfig;

/** Singleton that provides references to the database
 * Created by quentin on 03.11.16.
 */

public class DatabaseRef{

    private final static String mMediaDirectoryString = "MediaDirectory_quentin";
    private final static DatabaseReference mMediaDirectory = FirebaseDatabase.getInstance().getReference(mMediaDirectoryString);

    private final static String mUsersDirectoryString = "UsersDirectory";
    private final static DatabaseReference mUsersDirectory = FirebaseDatabase.getInstance().getReference(mUsersDirectoryString);


// PUBLIC METHODS

    public static DatabaseReference getMediaDirectory(){
        if(BuildConfig.DEBUG){

        }
        return mMediaDirectory;
    }

    public static DatabaseReference getUsersDirectory(){
        return mUsersDirectory;
    }

    public static DatabaseReference getRootDirectory() {
        return FirebaseDatabase.getInstance().getReference();
    }


    /* methods for deletion */

    public static void deletePhotoObjectFromDB(String pictureID){
        mMediaDirectory.child(pictureID).removeValue();
    }

    public static void deleteUserFromDB(String userID){
        mUsersDirectory.child(userID).removeValue();
    }

// CONSTRUCTOR FOR SINGLETON
    private DatabaseRef(){
        //empty
    }
}

