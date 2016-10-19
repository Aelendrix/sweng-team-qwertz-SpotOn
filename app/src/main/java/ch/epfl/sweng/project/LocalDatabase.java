package ch.epfl.sweng.project;

import android.location.Location;
import android.util.Log;
import android.util.SparseArray;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class LocalDatabase {

    private String dataPath;
    private Map<String,PhotoObject> photoDataMap = new HashMap<>();
    // Firebase instance variables
    private DatabaseReference myDBref;

    public LocalDatabase(String path){
        dataPath=path;
        myDBref = FirebaseDatabase.getInstance().getReference(dataPath);

    }
    //refresh the db from the server
    //later will take lat and lng as parameter to filter the data retrieved
    public void refresh(Location phoneLocation){
        //create a single event listener which return a list of object PhotoObject and loop over it
        //to add in our DB
        //TODO: nico filter the photo object retrieved in function of Time and location
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot photoSnapshot: dataSnapshot.getChildren()) {
                    PhotoObjectStoredInDatabase photoWithoutPic = photoSnapshot.getValue(PhotoObjectStoredInDatabase.class);
                    PhotoObject photoObject = photoWithoutPic.convertToPhotoObject();
                    addPhotoObject(photoObject);
                }
                Log.d("LocalDB",dataSnapshot.getChildrenCount()+" photoObjects added");

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
        if(!photoDataMap.containsKey(photo.getPictureId()))
        photoDataMap.put(photo.getPictureId(),photo);
    }

    public void deletePhotoObject(PhotoObject photo)
    {
        photoDataMap.remove(photo.getPictureId());
    }

    public Map<String,PhotoObject> getMap()
    {
        return photoDataMap;
    }
}