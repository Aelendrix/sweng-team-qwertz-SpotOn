package ch.epfl.sweng.spotOn.gui;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.location.Location;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabaseListener;
import ch.epfl.sweng.spotOn.localisation.ConcreteLocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTrackerListener;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.utils.ToastProvider;

public class MapFragment extends Fragment implements LocationTrackerListener, LocalDatabaseListener, OnMapReadyCallback,
        ClusterManager.OnClusterItemClickListener<Pin>, ClusterManager.OnClusterItemInfoWindowClickListener<Pin>,
        ClusterManager.OnClusterClickListener<Pin> {

    //Geneva Lake
    private static final LatLng DEFAULT_LOCATION = new LatLng(46.5,6.6);

    //marker representing our location on the map
    private Marker mLocationMarker;

    //list of photoObject
    private List<PhotoObject> mListPhoto;

    private List<String> mThumbIDs;
    private ClusterManager<Pin> mClusterManager;
    private GoogleMap mMap;

    private View mView;

    // limit the number of refreshes per second
    private long mLastRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // add as listener
        if(!LocalDatabase.instanceExists() || !ConcreteLocationTracker.instanceExists()){
            throw new IllegalStateException(("MapFragment can't function if the LocalDatabase and LocationTracker singletons aren't instantiated"));
        }
        ConcreteLocationTracker.getInstance().addListener(this);
        LocalDatabase.getInstance().addListener(this);
        mLastRefresh= Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View mView =  inflater.inflate(R.layout.activity_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        return mView;*/

        if(mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }
        try {
            mView = inflater.inflate(R.layout.activity_maps, container, false);
            SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map_fragment);
            mapFragment.getMapAsync(this);
        }catch (InflateException e) {

        }
        return mView;

    }

    /*
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Fragment f = this.getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (f != null) {
            getFragmentManager().beginTransaction().remove(f).commit();
            public void onProviderDisabled(String provider) {}
        };
    */

    /** function used to refresh the local location variable
     *  and apply it to our special marker on the map   */
    public void refreshMapLocation() {
        if(ConcreteLocationTracker.getInstance().hasValidLocation()){
            final LatLng newLocation = ConcreteLocationTracker.getInstance().getLatLng();
            if(mMap!=null){
                Handler tempHandler = new Handler(Looper.getMainLooper());
                if(mLocationMarker==null){
                    mLocationMarker = mMap.addMarker(new MarkerOptions()
                            .title("position")
                            .position(newLocation)
                            .anchor(0.5f,0.5f)
                            .zIndex(10f)
                            .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(getContext(),
                                    R.drawable.ic_position_marker_30dp))));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                }else{
                    tempHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLocationMarker.setPosition(newLocation);
                        }
                    });
                }
                tempHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mLocationMarker.setVisible(true);
                    }
                });
            }
        }
    }

    /*Manipulates the map once available.
    * Create the fake markers and mark my position
    */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set a preference for minimum and maximum zoom.
        mMap.setMinZoomPreference(8.0f);
        if(ConcreteLocationTracker.getInstance().hasValidLocation()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ConcreteLocationTracker.getInstance().getLatLng(), 12.0f));
        }
        else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 12.0f));
        }
        setUpCluster();
        refreshMapLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap == null) {
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment)).getMapAsync(this);
        }
    }

    /**
     * Set up the cluster manager
     */
    private void setUpCluster(){

        mClusterManager = new ClusterManager<>(getContext(), mMap, new MarkerManager(mMap){

            /**
             * Method called when clicking a marker that belongs in the mClusterManager: resolves
             * the bug that clicking on a marker from a non rendered cluster displayed nothing.
             * @param marker the clicked marker
             * @return the result of onClusterItemClick on the pin associated to the marker.
             */
            @Override
            public boolean onMarkerClick(Marker marker){
                //Get the map matching each marker (title) to the corresponding pin
                Map<String, Pin> markerPinMap = ClusterRenderer.getMarkerPinMap();
                Log.d("MarkerManager", String.valueOf(markerPinMap.size()));
                if(markerPinMap.containsKey(marker.getTitle())) {
                    //Get the corresponding pin from the clicked marker and retrun result of onClusterItemClick
                    Pin associatedPin = markerPinMap.get(marker.getTitle());
                    return onClusterItemClick(associatedPin);
                } else {
                    throw new NullPointerException("The clicked marker should be in the map of (marker, pin) but is not");
                }
            }
        });

        //The cluster manager takes care when the user clicks on a marker and regroups the markers together
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setRenderer(new ClusterRenderer(getContext(), mMap, mClusterManager));

        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addDBMarkers();
    }

    /**
     * use our local database to add the corresponding pins in the cluster manager
     */
    public void addDBMarkers()
    {
        if(ConcreteLocationTracker.instanceExists() && ConcreteLocationTracker.getInstance().hasValidLocation()){
            LatLng currLoc = ConcreteLocationTracker.getInstance().getLatLng();
            mListPhoto = new ArrayList<>(LocalDatabase.getInstance().getAllNearbyMediasMap().values());
            mThumbIDs = new ArrayList<>(LocalDatabase.getViewableThumbnails().keySet());
            // old if(mMap!=null && currLoc!=null) {
            if(mMap!=null) {
                //empty the cluster manager and the map of markers to pins
                mClusterManager.clearItems();
                //mMarkersToPin = new HashMap<>();

                //add the new pins on the Cluster Manager
                for (PhotoObject photo : mListPhoto) {
                    boolean canActivateIt = photo.isInPictureCircle(currLoc);
                    Pin pinForPicture = new Pin(photo, canActivateIt);
                    //add the pin to the cluster manager
                    mClusterManager.addItem(pinForPicture);
                    //Re-cluster the cluster at each addition of a pin
                    mClusterManager.cluster();
                }
            }else{
                Log.d("MapFragment","No valid instance of LocationTracker, or no valid Location");
            }
        }

    }

    /**
     * Method that associates and display the thumbnail of the photo associated to a marker when clicked
     * @param pin the pin/marker the user is clicking on
     * @return false (do not change it)
     */
    @Override
    public boolean onClusterItemClick(Pin pin) {
        Log.d("onClusterItemClick", "accessed1");
        mMap.setInfoWindowAdapter(new PhotoOnMarker(this.getContext(), pin));
        //If the marker clicked is yellow
        if (!pin.getAccessibility()) {
            Toast.makeText(getContext(), "Get closer to this point to see the picture", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    // LISTENER METHODS
    @Override
    public void updateLocation(Location newLocation) {
        refreshMapLocation();
    }

    @Override
    public void locationTimedOut(Location old) {
        Log.d("MapFragment","Listener says location timed out");
    }

    @Override
    public void databaseUpdated() {
        addDBMarkers();
    }

    /**
     * Method called when clicking the info window of a pin. It will go to the ViewFullSizeImageActivity
     * to display the full size image associated to the info window clicked
     * @param pin the pin/marker the user is clicking on its info window
     */
    @Override
    public void onClusterItemInfoWindowClick(Pin pin){
        String thumbID = pin.getPhotoObject().getPictureId();
        ImageAdapter imgAdapter = SeePicturesFragment.getImageAdapter();
        if(imgAdapter.containsThumbID(thumbID)) {
            SeePicturesFragment.mDefaultItemPosition = imgAdapter.getPositionThumbID(thumbID);
        } else {
            Log.d("Thumbnail", "thumbnail clicked not in the list");
        }
        Intent displayFullSizeImageIntent = new Intent(this.getActivity(), ViewFullSizeImageActivity.class);
//        displayFullSizeImageIntent.putExtra(ViewFullSizeImageActivity.WANTED_IMAGE_PICTUREID, thumbID);
        startActivity(displayFullSizeImageIntent);
    }

    /**
     * This methods needs to be implemented so it makes sure that clicking a marker displays nothing
     * Corrects the following bug: clicking on a green pin (with info window) and then clicking on a
     * cluster displayed the info window of the pin.
     * @param cluster the cluster that is clicked on
     * @return true -> clicking on a cluster does nothing
     */
    @Override
    public boolean onClusterClick(Cluster<Pin> cluster){
        if(mMap!=null){
            CameraUpdate center = CameraUpdateFactory.newLatLng(cluster.getPosition());
            mMap.moveCamera(center);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    cluster.getPosition(), (float) Math.floor(mMap
                            .getCameraPosition().zoom + 1)), 300,
                    null);
        }
        return true;
    }

    /**
     * Get a bitmap from a VectorDrawable (xml file) -> this method will be called if the API is
     * Lollipop or below
     * @param vectorDrawable the VectorDrawable to get its bitmap
     * @return the bitmap of the VectorDrawable
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    /**
     * Get the bitmap from an xml file to be the icon of the position marker
     * @param context the context of the fragment
     * @param drawableId the ID of the xml file
     * @return the bitmap of the resource xml file
     */
    private static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }
}
