package ch.epfl.sweng.spotOn.gui;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabaseListener;
import ch.epfl.sweng.spotOn.localisation.LocationTracker;
import ch.epfl.sweng.spotOn.localisation.LocationTrackerListener;
import ch.epfl.sweng.spotOn.media.PhotoObject;


public class MapFragment extends Fragment implements LocationTrackerListener, LocalDatabaseListener, OnMapReadyCallback, ClusterManager.OnClusterItemClickListener<Pin> {

    //Geneva Lake
    private static final LatLng DEFAULT_LOCATION = new LatLng(46.5,6.6);

    //marker representing our location on the map
    private Marker mLocationMarker;

    //list of photoObject
    private List<PhotoObject> listPhoto;
    private ClusterManager<Pin> mClusterManager;
    private Pin mClickedClusterPin;
    private GoogleMap mMap;

    private View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // add as listener
        if(!LocalDatabase.instanceExists() || !LocationTracker.instanceExists()){
            throw new IllegalStateException(("MapFragment can't function if the LocalDatabase and LocationTracker singletons aren't instanciated"));
        }
        LocationTracker.getInstance().addListener(this);
        LocalDatabase.getInstance().addListener(this);
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
        if(LocationTracker.getInstance().hasValidLocation()){
            LatLng newLocation = LocationTracker.getInstance().getLatLng();
            if(mMap!=null){
                if(mLocationMarker==null){
                    mLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(newLocation)
                            .title("My position")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                }else{
                    mLocationMarker.setPosition(newLocation);
                }
                mLocationMarker.setVisible(true);
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
        mMap.setMinZoomPreference(5.0f);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION,10.0f));
        setUpCluster();
        refreshMapLocation();
        //This will call the method onMarkerClick when clicking a marker
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        // new
        if(!LocationTracker.instanceExists()){
            throw new IllegalStateException("Location tracker not initialized");
        }
        if (mMap == null) {
            ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment)).getMapAsync(this);
        }
    }

    /**
     * Set up the cluster manager
     */
    private void setUpCluster(){
        mClusterManager = new ClusterManager<Pin>(getContext(), mMap);
        //The cluster manager takes care when the user clicks on a marker and regroups the markers together
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        //Displays the right color to the markers (green or yellow)
        mClusterManager.setRenderer(new ClusterRenderer(getContext(), mMap, mClusterManager));
        mClusterManager.setOnClusterItemClickListener(this);
        addDBMarkers();
    }

    /**
     * use our local database to add the corresponding pins in the cluster manager
     */
    public void addDBMarkers()
    {
        LatLng currLoc = LocationTracker.getInstance().getLatLng();
        listPhoto = new ArrayList<>(LocalDatabase.getInstance().getAllNearbyMediasMap().values());
        // old if(mMap!=null && currLoc!=null) {
        if(mMap!=null) {
            //empty the cluster manager
            mClusterManager.clearItems();
            //add the new markers on the Cluster Manager
            for (PhotoObject photo : listPhoto) {
                boolean canActivateIt = photo.isInPictureCircle(currLoc);
                LatLng photoPosition = new LatLng(photo.getLatitude(),photo.getLongitude());
                BitmapDescriptor color;
                //add a GREEN pin to the cluster manager if the photo can be seen
                if(canActivateIt) {
                    color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                }
                //add a YELLOW pin to the cluster manager if it can't be activated to see the picture
                else{
                    color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                }
                Pin pinForPicture = new Pin(photo, canActivateIt);
                //add the marker to the cluster manager
                mClusterManager.addItem(pinForPicture);
                mClusterManager.cluster();
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
        mClickedClusterPin = pin;
        mMap.setInfoWindowAdapter(new PhotoOnMarker(this.getContext(), pin));
        //If the marker clicked is yellow
        if(!pin.getAccessibility()) {
            Toast.makeText(getContext(), "Get closer to this point to see the picture", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     * Display a circle around each marker on the map representing the radius
     * where the picture is visible
     * @param picture the photoObject on which the circle will be set
     */
    public void displayCircleForPicture(PhotoObject picture){
        if (picture != null) {
            if(mMap!=null) {
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(picture.getLatitude(), picture.getLongitude()))
                        .radius(picture.getRadius())
                        .strokeColor(Color.RED));
            }
        }
    }

    /**
     * Display a marker on the map at the location where the picture was taken
     * and displays the bitmap image when clicking the marker
     * @param photos the list of photos we represent on the map
     */
    public void displayPictureMarkers(ArrayList<PhotoObject> photos){
        if(!photos.isEmpty()) {
            for (int i = 0; i < photos.size(); i++) {
                PhotoObject obj = photos.get(i);
                LatLng picSpot = new LatLng(obj.getLatitude(), obj.getLongitude());
                displayCircleForPicture(obj);
                if(mMap!=null) {
                    mMap.addMarker(new MarkerOptions()
                            .position(picSpot)
                            .title(obj.getPhotoName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }
            }
        }
    }


    // LISTENER METHODS
    @Override
    public void updateLocation(Location newLocation) {
        refreshMapLocation();
    }

    @Override
    public void locationTimedOut() {
        Log.d("MapFragment","Listener says location timed out");
    }

    @Override
    public void databaseUpdated() {
        addDBMarkers();
    }
}
