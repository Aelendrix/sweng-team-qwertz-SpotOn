package ch.epfl.sweng.spotOn.test.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

/** singleton that provides database directories that will be used for storing test objects
 * Created by quentin on 04.11.16.
 */

public class DatabaseRef_Test {

    private final static String mMediaDirectoryString = "MediaDirectory_test";
    private final static DatabaseReference mMediaDirectory = FirebaseDatabase.getInstance().getReference(mMediaDirectoryString);

    private final static String mUsersDirectoryString = "UsersDirectory_test";
    private final static DatabaseReference mUsersDirectory = FirebaseDatabase.getInstance().getReference(mUsersDirectoryString);


// PUBLIC METHODS

    public static DatabaseReference getMediaDirectory(){
        return mMediaDirectory;
    }

    public static DatabaseReference getUsersDirectory(){
        return mUsersDirectory;
    }


    // CONSTRUCTOR FOR SINGLETON
    private DatabaseRef_Test(){
        //empty
    }


}
