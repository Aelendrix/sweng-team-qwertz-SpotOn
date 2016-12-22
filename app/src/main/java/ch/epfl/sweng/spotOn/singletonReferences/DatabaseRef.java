package ch.epfl.sweng.spotOn.singletonReferences;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        return mMediaDirectory;
    }

    public static DatabaseReference getUsersDirectory(){
        return mUsersDirectory;
    }

    public static DatabaseReference getRootDirectory() {
        return FirebaseDatabase.getInstance().getReference();
    }

    /**
     * delete a photoObject depending of his pictureID
     * @param pictureID a unique ID linked of a picture in the firebase DB
     */
    public static void deletePhotoObjectFromDB(String pictureID){

        if(pictureID == null){
            throw new IllegalArgumentException("Error in DatabaseRef: deletePhotoObjectFromDB, pictureId is null");
        } else {
            mMediaDirectory.child(pictureID).removeValue();
        }
    }

    /**
     * delete a photoObject depending of his pictureID
     * @param userID a unique ID linked of a user in the firebase DB; the ID is the real facebookID if the user.
     */
    public static void deleteUserFromDB(String userID){
        mUsersDirectory.child(userID).removeValue();
    }

// CONSTRUCTOR FOR SINGLETON
    private DatabaseRef(){
    }
}

