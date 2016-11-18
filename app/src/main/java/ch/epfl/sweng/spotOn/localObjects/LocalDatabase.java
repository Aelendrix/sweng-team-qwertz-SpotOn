package ch.epfl.sweng.spotOn.localObjects;


import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTrackerListener;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.media.PhotoObjectStoredInDatabase;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;


public class LocalDatabase implements LocationTrackerListener{

    private static LocalDatabase mSingleInstance = null;

    private Map<String,PhotoObject> mediaDataMap;
    private Map<String, PhotoObject> mViewableMediaDataMap;
    private List<LocalDatabaseListener> mListeners;

    private Location mCachedLocation;

    private LocationTracker refToLocationTracker;

    // these settings help to avoid unnecessary refreshes of the database
    private final static int TIME_INTERVAL_FOR_MAXIMUM_REFRESH_RATE = 2*1000; // refresh the localdatabase at most every 1 seconds on position changes
    private final static int TIME_INTERVAL_FOR_MINIMUM_REFRESH_RATE = 2*60*1000; // refresh at least every 2 minutes
    private final static int MINIMUM_DISTANCE_REFRESH_THRESHOLD = 5; // won't refresh if the last Location was closer than this (don't refresh due to "noise" in the Location sensors)

    private final static double FETCH_RADIUS = 0.1; // the radius in which we fetch pictures, in degrees



    // CONSTRUCTOR / INITIALIZE()
    public static void initialize(LocationTracker l){
            mSingleInstance = new LocalDatabase(l);
            l.addListener(mSingleInstance);
            mSingleInstance.setAutoRefresh();
    }

    private LocalDatabase(LocationTracker l) {
        mediaDataMap = new HashMap<>();
        mListeners = new ArrayList<>();
        mViewableMediaDataMap = new HashMap<>();
        refToLocationTracker = l;
    }




    //PUBLIC METHODS
    public static boolean instanceExists(){
        return mSingleInstance!=null;
    }

    public static LocalDatabase getInstance(){
        if(mSingleInstance==null){
            throw new IllegalStateException("Localdatabase not initialized yet");
        }
        return mSingleInstance;
    }

    public boolean hasKey(String key){
        return mediaDataMap.containsKey(key);
    }

    public PhotoObject get(String key){
        return mediaDataMap.get(key);
    }

    public void addPhotoObject(PhotoObject photo){
        if(!mediaDataMap.containsKey(photo.getPictureId())) {
            mediaDataMap.put(photo.getPictureId(), photo);
            Location photoLocation = new Location("mockProvider");
            photoLocation.setLatitude(photo.getLatitude());
            photoLocation.setLongitude(photo.getLongitude());
            if(refToLocationTracker.getLocation().distanceTo(photoLocation) < photo.getRadius()) {
                mViewableMediaDataMap.put(photo.getPictureId(), photo);
            }
        }
    }

    /** adds the PhotoObject 'newObject' if it is within a radius of FETCH_PICTURES_RADIUS
     *  of the current cached location
     */
    public void testAndAddPhotoObject(PhotoObject newObject) {
        if(!refToLocationTracker.hasValidLocation()){
            Log.d("LocalDatabase", "No valid location, can't refresh DB");
        }else {
            mCachedLocation = refToLocationTracker.getLocation();
            if (Math.abs(newObject.getLatitude() - mCachedLocation.getLatitude()) < FETCH_RADIUS
                    && Math.abs(newObject.getLongitude() - mCachedLocation.getLongitude()) < FETCH_RADIUS) {
                if (!mediaDataMap.containsKey(newObject.getPictureId())) {
                    mediaDataMap.put(newObject.getPictureId(), newObject);
                }
            }
        }
    }

    /** return a map containing all PhotoObjects whose radius is wide enough to be visible
     *  from the currently cached location */
    public Map<String, PhotoObject> getViewableMedias(){
        return mViewableMediaDataMap;
    }

    public Map<String, Bitmap> getViewableThumbmails() {
        HashMap<String, Bitmap> resultMap = new HashMap<>();
        for(PhotoObject p : mViewableMediaDataMap.values()){
            resultMap.put(p.getPictureId(), p.getThumbnail());
        }
        return resultMap;
    }

    /** return the map of all nearby photos, typically used to display out of range pins ont the map */
    public Map<String,PhotoObject> getAllNearbyMediasMap(){
        return mediaDataMap;
    }

    /** remove a photoObject from the LocalDatabase */
    public void removePhotoObject(String key){
        mediaDataMap.remove(key);
    }

    public void addListener(LocalDatabaseListener l){
        mListeners.add(l);
        l.databaseUpdated();
    }



// PRIVATE METHODS
    /** notifies all listeners of a change of the database content */
    private void notifyListeners(){
        for(LocalDatabaseListener l : mListeners){
            l.databaseUpdated();
        }
    }

    /** clears all data from the LocalDatabase */
    public void clear() {
        mediaDataMap.clear();
        mViewableMediaDataMap.clear();
        notifyListeners();
    }

    /** refreshes the map of viewable photo */
    private void refreshViewablePhotos() {
        for(PhotoObject po : mediaDataMap.values()){
            // create a "location" object for the photo
            Location newObjectLocation = new Location("dummyProvider");
            newObjectLocation.setLatitude(po.getLatitude());
            newObjectLocation.setLongitude(po.getLongitude());
            // compare it with the location provided by the LocationTracker
            if(newObjectLocation.distanceTo(mCachedLocation) < po.getRadius()) {
                if(!mViewableMediaDataMap.containsKey(po.getPictureId())) {
                    mViewableMediaDataMap.put(po.getPictureId(), po);
                }
            }
        }
    }

    /** used during initialization, adds a listener to the firebase directory containing the medias */
    private void setAutoRefresh(){
        Query photoSortedByTime = DatabaseRef.getMediaDirectory().orderByChild("expireDate").startAt(new Date().getTime());
        photoSortedByTime.addValueEventListener(getFirebaseValueEventListener());
    }

    /** adds a single-use listener to the firebase directory containing the medias  */
    private void forceSingleRefresh(){
        Query photoSortedByTime = DatabaseRef.getMediaDirectory().orderByChild("expireDate").startAt(new Date().getTime());
        photoSortedByTime.addListenerForSingleValueEvent(getFirebaseValueEventListener());
    }

    /** a listener that updates the LocalDatabase when firebase data changes    */
    private ValueEventListener getFirebaseValueEventListener(){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!refToLocationTracker.hasValidLocation()){
                    Log.d("LocalDatabase","can't refresh, LocationProvider has no valid Location");
                }else if(refreshingDBisWorthIt(refToLocationTracker.getLocation())){
                    LocalDatabase.getInstance().clear();
                    for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                        PhotoObjectStoredInDatabase photoWithoutPic = photoSnapshot.getValue(PhotoObjectStoredInDatabase.class);
                        PhotoObject photoObject = photoWithoutPic.convertToPhotoObject();
                        LocalDatabase.getInstance().testAndAddPhotoObject(photoObject);
                    }
                    Log.d("LocalDB", LocalDatabase.getInstance().getAllNearbyMediasMap().size() + " photoObjects added");
                    LocalDatabase.getInstance().refreshViewablePhotos();
                    LocalDatabase.getInstance().notifyListeners();
                }else{
                    Log.d("LocalDatabase"," prevented from refreeshing too often");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                // todo - handle exceptional behavior
            }
        };
    }

    private boolean refreshingDBisWorthIt(Location newLocation){
        boolean tooLongWithoutRefreshing = mCachedLocation.getTime() - newLocation.getTime() > TIME_INTERVAL_FOR_MINIMUM_REFRESH_RATE;
        boolean refreshingTooOften = mCachedLocation.getTime() - newLocation.getTime() > TIME_INTERVAL_FOR_MAXIMUM_REFRESH_RATE;
        boolean travelledFarEnoughForARefresh = mCachedLocation.distanceTo(newLocation) > MINIMUM_DISTANCE_REFRESH_THRESHOLD;

        return tooLongWithoutRefreshing || (!refreshingTooOften && travelledFarEnoughForARefresh);
    }



// LISTENER FUNCTIONS
    @Override
    public void updateLocation(Location newLocation) {
        if (mCachedLocation == null) {
            Log.d("Localdatabase", "location updated, forcing single refresh");
            mCachedLocation = newLocation;
            forceSingleRefresh();
        } else {
            if(refreshingDBisWorthIt(newLocation)){
                Log.d("Localdatabase", "location updated, forcing single refresh");
                mCachedLocation = newLocation;
                forceSingleRefresh();
            }// otherwise, it's not worth it to refresh the database
        }
    }

    @Override
    public void locationTimedOut(){
        Log.d("Localdatabase","location timed out");
    }
}