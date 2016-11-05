package ch.epfl.sweng.spotOn.gui;


import android.graphics.Color;
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
import com.google.android.gms.maps.MapsInitializer;
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
import ch.epfl.sweng.spotOn.media.PhotoObject;

public class MapFragment extends Fragment implements OnMapReadyCallback, ClusterManager.OnClusterItemClickListener<Pin> {

    //Geneva Lake
    private static final LatLng DEFAULT_LOCATION = new LatLng(46.5,6.6);
    /*

    //fake Data
    //esplanade epfl (under one roof)
    private static final LatLng FAKE_SPOT_1 = new LatLng(46.519241, 6.565911);
    //moutons Unil
    private static final LatLng FAKE_SPOT_2 = new LatLng(46.521002, 6.575986);
    //centre sportif
    private static final LatLng FAKE_SPOT_3 = new LatLng(46.519403, 6.579841);
    //Flon
    private static final LatLng FAKE_SPOT_4 = new LatLng(46.520844, 6.630718);
     */
    //local location variable
    private LatLng mPhoneLatLng;
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
        //setRetainInstance(true);
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
    /**
     * function used to refresh the local location variable
     * and apply it to our special marker on the map
     *  @param phoneLatLng the location of the user using the GPS
     */
    public void refreshMapLocation(LatLng phoneLatLng) {

        if (phoneLatLng != null) {
            //now apply the location to the map
            mPhoneLatLng = new LatLng(phoneLatLng.latitude,phoneLatLng.longitude);

            if (mMap != null) {
                //change the localisation cursor, if null, create one instead
                if (mLocationMarker == null) {
                    mLocationMarker = mMap.addMarker(new MarkerOptions()
                                        .position(mPhoneLatLng)
                                        .title("My position")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mPhoneLatLng));
                } else {
                    mLocationMarker.setPosition(mPhoneLatLng);
                }
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
        //This will call the method onMarkerClick when clicking a marker
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mPhoneLatLng == null && LocalDatabase.getLocation() != null){
            mPhoneLatLng = new LatLng(LocalDatabase.getLocation().getLatitude(),LocalDatabase.getLocation().getLongitude());
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
        listPhoto = new ArrayList<>(LocalDatabase.getMap().values());
        if(mMap!=null && mPhoneLatLng!=null) {
            //empty the cluster manager
            mClusterManager.clearItems();
            refreshMapLocation(mPhoneLatLng);
            //add the new markers on the Cluster Manager
            for (PhotoObject photo : listPhoto) {
                boolean canActivateIt = photo.isInPictureCircle(mPhoneLatLng);
                LatLng photoPosition = new LatLng(photo.getLatitude(),photo.getLongitude());
                Pin pinForPicture = new Pin(photo, canActivateIt);
                //add the marker to the cluster manager
                mClusterManager.addItem(pinForPicture);
                //Re-cluster the cluster at each addition of a pin
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
}
