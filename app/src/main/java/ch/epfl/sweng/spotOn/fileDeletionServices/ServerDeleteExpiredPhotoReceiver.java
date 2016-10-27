package ch.epfl.sweng.spotOn.fileDeletionServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * Created by Bruno on 20/10/2016.
 *
 * This class implements the deletion of expired data on the FireBase DataBase. The process is
 * launched when an alarm goes off.
 *
 */

public class ServerDeleteExpiredPhotoReceiver extends BroadcastReceiver {

    private final String PATH_TO_MEDIA_DIRECTORY = "MediaDirectory";
    private final FirebaseDatabase mDB = FirebaseDatabase.getInstance();
    private final String VALUE_TO_CHECK = "expireDate";

    @Override
    public void onReceive(Context context, Intent intent) {
        Query query = mDB.getReference(PATH_TO_MEDIA_DIRECTORY).orderByChild(VALUE_TO_CHECK);

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        HashMap<String, Long> resultAsMap = ((HashMap<String, Long>) child.getValue());
                        long expireTime = resultAsMap.get(VALUE_TO_CHECK);
                        if(expireTime < System.currentTimeMillis()){
                            child.child(VALUE_TO_CHECK).getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
