package ch.epfl.sweng.project;

import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import android.util.SparseArray;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalDatabase {

    private final static String dataPath = "MediaDirectory";
    private final static Map<String,PhotoObject> photoDataMap = new HashMap<>();
    // Firebase instance variables
    private final static DatabaseReference myDBref = FirebaseDatabase.getInstance().getReference(dataPath);
    private static Location mLocation;

    private LocalDatabase() {
    }

    //refresh the db from the server
    public static void refresh(Location phoneLocation,TabActivity tab){
        Log.d("LocalDB"," refreshing DB");
        //create a single event listener which return a list of object PhotoObject and loop over it
        //to add in our DB
        final double maxRadius =0.1;// in degree
        final double longitude = phoneLocation.getLongitude();
        final double latitude = phoneLocation.getLatitude();
        final TabActivity tabActivity = tab;
        //Query photoSortedByLongitude = myDBref.orderByChild("longitude").startAt(longitude-maxRadius).endAt(longitude+maxRadius);
        //get photo still alive
        java.util.Date date= new java.util.Date();
        Query photoSortedByTime = myDBref.orderByChild("expireDate").startAt(date.getTime());
        ValueEventListener dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot photoSnapshot: dataSnapshot.getChildren()) {
                    PhotoObjectStoredInDatabase photoWithoutPic = photoSnapshot.getValue(PhotoObjectStoredInDatabase.class);
                    PhotoObject photoObject = photoWithoutPic.convertToPhotoObject();
                    Log.d("LocalDB",photoObject.toString());
                    //filter the photo in function of the location
                    double photoLat = photoObject.getLatitude();
                    double photoLng = photoObject.getLongitude();
                    if(photoLat-maxRadius<latitude && latitude < photoLat+maxRadius) {
                        if(photoLng-maxRadius<longitude && longitude < photoLng+maxRadius) {
                            addPhotoObject(photoObject);
                        }
                    }
                }
                Log.d("LocalDB",dataSnapshot.getChildrenCount()+" photoObjects added");
                //refresh the child of tabActivity
                tabActivity.endRefreshDB();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        photoSortedByTime.addListenerForSingleValueEvent(dataListener);
    }

    public static void addPhotoObject(PhotoObject photo)
    {
        if(!photoDataMap.containsKey(photo.getPictureId())) {
            photoDataMap.put(photo.getPictureId(), photo);
        }
    }

    public static void deletePhotoObject(PhotoObject photo)
    {
        photoDataMap.remove(photo.getPictureId());
    }

    public static Map<String,PhotoObject> getMap()
    {
        return photoDataMap;
    }

    public static List<Pair<Bitmap, String>> getThumbnailArray(){
        List<PhotoObject> listPhoto = new ArrayList<>(photoDataMap.values());
        List<Pair<Bitmap, String>> listThumbnail = new ArrayList<>();
        for(PhotoObject o : listPhoto){
            listThumbnail.add(new Pair<Bitmap, String>(o.getThumbnail(), o.getPictureId()));
        }
        return listThumbnail;
    }

    public static void setLocation(Location location) {
        mLocation = location;
    }

    public static Location getLocation() {
        return mLocation;
    }

    public static boolean hasKey(String pictureId){
        return photoDataMap.containsKey(pictureId);
    }

    public static PhotoObject getPhoto(String pictureId){
        return photoDataMap.get(pictureId);
    }
}