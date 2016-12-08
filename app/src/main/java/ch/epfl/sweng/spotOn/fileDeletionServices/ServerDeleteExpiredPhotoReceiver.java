package ch.epfl.sweng.spotOn.fileDeletionServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;

/**
 * Created by Bruno on 20/10/2016.
 *
 * This class implements the deletion of expired data on the FireBase DataBase. The process is
 * launched when an alarm goes off.
 *
 */
public class ServerDeleteExpiredPhotoReceiver extends BroadcastReceiver {

    private final String VALUE_TO_CHECK = "expireDate";

    @Override
    public void onReceive(Context context, Intent intent) {
        Query query = DatabaseRef.getMediaDirectory().orderByChild(VALUE_TO_CHECK).endAt(System.currentTimeMillis());
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    String pictureID = child.getKey();
                    DatabaseRef.deletePhotoObjectFromDB(pictureID);
                    StorageRef.deletePictureFromStorage(pictureID);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
