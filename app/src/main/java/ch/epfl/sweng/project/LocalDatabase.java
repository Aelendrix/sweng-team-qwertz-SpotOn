package ch.epfl.sweng.project;

import android.util.Log;
import android.util.SparseArray;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LocalDatabase {

    private String dataPath;
    private SparseArray<PhotoObject> photoDataMap = new SparseArray<>();
    // Firebase instance variables
    private DatabaseReference myDBref;

    public LocalDatabase(String path){
        dataPath=path;
        myDBref = FirebaseDatabase.getInstance().getReference(dataPath);

    }
    //refresh the db from the server
    //later will take lat and lng as parameter to filter the data retrieved
    public void refresh(){
        //create a single event listener which return a list of object PhotoObject and loop over it
        //to add in our DB
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot photoSnapshot: dataSnapshot.getChildren()) {
                    PhotoObject photo = photoSnapshot.getValue(PhotoObject.class);
                    addPhotoObject(photo);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        myDBref.addListenerForSingleValueEvent(dataListener);
    }

    public void addPhotoObject(PhotoObject photo)
    {
        photoDataMap.append(photo.getPictureId(),photo);
    }

    public void deletePhotoObject(PhotoObject photo)
    {
        photoDataMap.delete(photo.getPictureId());
    }
}
