package ch.epfl.sweng.spotOn.localObjects;


import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTrackerListener;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.media.PhotoObjectStoredInDatabase;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.user.User;
import ch.epfl.sweng.spotOn.user.UserManager;


public class LocalDatabase implements LocationTrackerListener{

    private static LocalDatabase mSingleInstance = null;

    private Map<String,PhotoObject> mediaDataMap;
    private static Map<String, PhotoObject> mViewableMediaDataMap;
    private List<LocalDatabaseListener> mListeners;

    private Location mCachedLocation;

    private long mLastRefreshDate;

    private LocationTracker refToLocationTracker;

    // these settings help to avoid unnecessary refreshes of the database
    private final static int TIME_INTERVAL_FOR_MAXIMUM_REFRESH_RATE_FIREBASE = 1500; // refresh the localDatabase at most every 1.5 seconds
    private final static int TIME_INTERVAL_FOR_MAXIMUM_REFRESH_RATE_LOCATION = 3*1000; // refresh the localDatabase at most every 3 seconds

    private final static int TIME_INTERVAL_FOR_MINIMUM_REFRESH_RATE = 3*60*1000; // refresh at least every 5 minutes
    private final static int MINIMUM_DISTANCE_REFRESH_THRESHOLD = 3; // won't refresh if the last Location was closer than this (don't refresh due to "noise" in the Location sensors)

    private final static double FETCH_RADIUS = 2*PhotoObject.MAX_VIEW_RADIUS; // the radius in which we fetch pictures, in km



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
        mLastRefreshDate = Calendar.getInstance().getTimeInMillis();
    }




    //PUBLIC METHODS
    public static boolean instanceExists(){
        return mSingleInstance!=null;
    }

    public static LocalDatabase getInstance(){
        if(mSingleInstance==null){
            throw new IllegalStateException("LocalDatabase not initialized yet");
        }
        return mSingleInstance;
    }

    public void refresh(){
        forceSingleRefresh();
    }

    public boolean hasKey(String key){
        return mediaDataMap.containsKey(key);
    }

    public PhotoObject get(String key){
        return mediaDataMap.get(key);
    }

    /** Adds a photoObject to the database, regardless of its position. NB : listeners need to be updated manually after that  */
    public void addPhotoObject(PhotoObject photo){
        mediaDataMap.put(photo.getPictureId(), photo);
        addToViewableMediaIfWithinViewableRange(photo);
    }

    /** remove a photoObject from the LocalDatabase - NB : listeners need to be updated manually after that  */
    public void removePhotoObject(String key){
        mediaDataMap.remove(key);
        if(mViewableMediaDataMap.containsKey(key)){
            mViewableMediaDataMap.remove(key);
        }
    }

   /** adds the PhotoObject 'newObject' if it is within a radius of FETCH_PICTURES_RADIUS of the current cached location
     *  NB : listeners need to be updated manually after that  */
    public void addIfWithinFetchRadius(PhotoObject newObject, Location databaseCachedLocation) {
        if( mCachedLocation!=null ) {
            if (newObject.obtainLocation().distanceTo(databaseCachedLocation) < FETCH_RADIUS) {
                if (!mediaDataMap.containsKey(newObject.getPictureId())) {
                    addPhotoObject(newObject);
                }
            }
        }else{
            Log.d("LocalDatabase", "WARNING - called addIfWithinFetchRadius(), but LocationTracker had no valid location");
        }
    }

    /** notifies all listeners of a change of the database content - public as it needs to be called in tests after manually adding objects*/
    public void notifyListeners(){
        if( ! mListeners.isEmpty() ){
            for(LocalDatabaseListener l : mListeners){
                l.databaseUpdated();
            }
        }
    }

    /** return a map containing all PhotoObjects whose radius is wide enough to be visible
     *  from the currently cached location */
    public Map<String, PhotoObject> getViewableMedias(){
        return mViewableMediaDataMap;
    }

    public static Map<String, Bitmap> getViewableThumbnails() {
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


    public void addListener(LocalDatabaseListener l){
        mListeners.add(l);
        l.databaseUpdated();
    }

    /** clears all data from the LocalDatabase */
    public void clear() {
        mediaDataMap.clear();
        mViewableMediaDataMap.clear();
        notifyListeners();
    }

// PRIVATE METHODS
    /** adds the media to the list of viewable media if within viewable range */
    private void addToViewableMediaIfWithinViewableRange(PhotoObject po){
        if(mCachedLocation==null){
            Log.d("LocalDatabase","WARNING : called addToViewableMediaIfWithinRadius() called while holding no valid cached location");
        }else {
            if (mCachedLocation.distanceTo(po.obtainLocation()) < po.getRadius()) {
                mViewableMediaDataMap.put(po.getPictureId(), po);
            }
        }
    }

    /** refreshes the map of viewable photo */
    private void refreshViewablePhotos() {
        if( mCachedLocation == null){
            Log.d("LocalDatabase", "WARNING : called refreshViewablePhotos, but no valid cachedLocation");
        }else {
            for (PhotoObject po : mediaDataMap.values()) {
                // compare the object's location with the location provided by the LocationTracker
                if (po.obtainLocation().distanceTo(mCachedLocation) < po.getRadius()) {
                    if (!mViewableMediaDataMap.containsKey(po.getPictureId())) {
                        mViewableMediaDataMap.put(po.getPictureId(), po);
                    }
                }
            }
        }
    }

    /** used during initialization, adds a listener to the firebase directory containing the medias */
    private void setAutoRefresh(){
        Query photoSortedByTime = DatabaseRef.getMediaDirectory().orderByChild("expireDate").startAt(new Date().getTime());
        photoSortedByTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if( mSingleInstance.allowRefreshAccordingToMaxRefreshRate() && mCachedLocation!= null){
                    Location mLocationTempCopy;
                    synchronized (this) {
                        mLocationTempCopy = new Location(mCachedLocation);
                    }
                    LocalDatabase.getInstance().clear();
                    String userID;
                    if(UserManager.getInstance().userIsLoggedIn()) {
                        userID = UserManager.getInstance().getUser().getUserId();
                    }
                    else{
                        userID = null;
                    }
                    for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                        PhotoObject photoObject = photoSnapshot.getValue(PhotoObjectStoredInDatabase.class).convertToPhotoObject();
                        if(userID == null){
                            LocalDatabase.getInstance().addIfWithinFetchRadius(photoObject, mLocationTempCopy);
                        }
                        else if(!(photoObject.getReportersList().contains(userID))) {
                            LocalDatabase.getInstance().addIfWithinFetchRadius(photoObject, mLocationTempCopy);
                        }
                    }
                    // refresh last refresh date
                    mLastRefreshDate = Calendar.getInstance().getTimeInMillis();
                    Log.d("LocalDatabase", "updated via firebase listener : "+LocalDatabase.getInstance().getAllNearbyMediasMap().size() + " photoObjects added");
                    LocalDatabase.getInstance().refreshViewablePhotos();
                    LocalDatabase.getInstance().notifyListeners();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                // todo - handle exceptional behavior
            }
        });
    }

    /** adds a single-use listener to the firebase directory containing the medias  */
    // NB : can't have a null mLocation, since this is only called when location updated
    private void forceSingleRefresh(){
        Query photoSortedByTime = DatabaseRef.getMediaDirectory().orderByChild("expireDate").startAt(new Date().getTime());
        photoSortedByTime.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mCachedLocation==null){
                    Log.d("LocalDatabase","WARNING : calling forceSingleRefresh() while holding no valid mCachedLocation ");
                }else {
                    LocalDatabase.getInstance().clear();
                    Location mLocationTempCopy;
                    synchronized (this) {
                        mLocationTempCopy = new Location(mCachedLocation);
                    }
                    String userID;
                    if(UserManager.getInstance().userIsLoggedIn()) {
                        userID = UserManager.getInstance().getUser().getUserId();
                    }
                    else{
                        userID = null;
                    }
                    for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                        PhotoObject photoObject = photoSnapshot.getValue(PhotoObjectStoredInDatabase.class).convertToPhotoObject();
                        if(userID == null){
                            LocalDatabase.getInstance().addIfWithinFetchRadius(photoObject, mLocationTempCopy);
                        }
                        else if(!(photoObject.getReportersList().contains(userID))) {
                            LocalDatabase.getInstance().addIfWithinFetchRadius(photoObject, mLocationTempCopy);
                        }
                    }
                    // refresh last refresh date
                    mLastRefreshDate = Calendar.getInstance().getTimeInMillis();
                    Log.d("LocalDatabase", "updated via force single refresh, " + LocalDatabase.getInstance().getAllNearbyMediasMap().size() + " photoObjects added");
                    LocalDatabase.getInstance().refreshViewablePhotos();
                    LocalDatabase.getInstance().notifyListeners();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
                // todo - handle exceptional behavior
            }
        });
    }

    private boolean allowRefreshAccordingToNewLocation(Location newLocation){
        if(mCachedLocation==null){
            return true;
        }else {
            long timeDiffBetweenCurrentAndNewLocations = Math.abs(newLocation.getTime() - mCachedLocation.getTime());

            boolean tooLongWithoutRefreshing = timeDiffBetweenCurrentAndNewLocations > TIME_INTERVAL_FOR_MINIMUM_REFRESH_RATE;
            boolean refreshingTooOften = timeDiffBetweenCurrentAndNewLocations < TIME_INTERVAL_FOR_MAXIMUM_REFRESH_RATE_LOCATION;
            boolean travelledFarEnoughForARefresh = mCachedLocation.distanceTo(newLocation) > MINIMUM_DISTANCE_REFRESH_THRESHOLD;

            return tooLongWithoutRefreshing || (!refreshingTooOften && travelledFarEnoughForARefresh);
        }
    }

    private boolean allowRefreshAccordingToMaxRefreshRate(){
        if(mCachedLocation==null){
            Log.d("LocalDatabase","Refresh based on maximum refresh rate couldn't be allowed : no Location available");
            return false;
        }else {
            long timeDiffSinceLastRefresh = Math.abs(mLastRefreshDate - Calendar.getInstance().getTimeInMillis());
            return timeDiffSinceLastRefresh > TIME_INTERVAL_FOR_MAXIMUM_REFRESH_RATE_FIREBASE;
        }
    }



// LISTENER FUNCTIONS
    @Override
    public void updateLocation(Location newLocation) {
        if(allowRefreshAccordingToNewLocation(newLocation)){
            synchronized (this) {
                mCachedLocation = newLocation;
            }
            Log.d("LocalDatabase", "location updated, forcing single refresh");
            forceSingleRefresh();
        }// otherwise, it's not worth it to refresh the database
    }

    @Override
    public void locationTimedOut(Location old){
        Log.d("LocalDatabase","listener notified that location timed out");
        synchronized (this) {
            mCachedLocation = null;
        }
    }
}