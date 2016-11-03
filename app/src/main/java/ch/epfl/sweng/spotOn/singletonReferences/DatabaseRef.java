package ch.epfl.sweng.spotOn.singletonReferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/** Singleton that provides references to the database
 * Created by quentin on 03.11.16.
 */

public class DatabaseRef {

    private final String mMediaDirectoryString = "mediaDirectory";
    private final DatabaseReference mMediaDirectory = FirebaseDatabase.getInstance().getReference(mMediaDirectoryString);

    private final String mUsersDirectoryString = "usersDirectory";
    private final DatabaseReference mUsersDirectory = FirebaseDatabase.getInstance().getReference(mUsersDirectoryString);


// PUBLIC METHODS

    public DatabaseReference getMediaDirectory(){
        return mMediaDirectory;
    }

    public DatabaseReference getUsersDirectory(){
        return mUsersDirectory;
    }


// CONSTRUCTOR FOR SINGLETON
    private DatabaseRef(){
        //empty
    }

}

