package ch.epfl.sweng.spotOn.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ch.epfl.sweng.spotOn.gui.TakePictureFragment;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;

/**
 * Created by Bruno on 17/11/2016.
 *
 * This class implements the refreshing of the number of remaining photos that the user can
 * post in a day each midnight.
 */

public class RefreshRemainingPhotosReceiver extends BroadcastReceiver {

    private final String userID = UserId.getInstance().getUserId();
    private final String KARMA = "karma";
    private final DatabaseReference DBRef = DatabaseRef.getUsersDirectory();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (userID != null) {
            Query query = DBRef.child(userID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        long karma = User.INITIAL_KARMA;
                        if (dataSnapshot.child(KARMA).getValue() != null) {
                            karma = ((long) dataSnapshot.child(KARMA).getValue());

                        } else {
                            DBRef.child(userID).child(KARMA).setValue(karma);
                        }
                        long remainingPhotos = User.computeMaxPhotoInDay(karma);
                        DBRef.child(userID).child("RemainingPhotos").setValue(remainingPhotos);
                        TakePictureFragment.setRemainingPhotos(remainingPhotos);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
