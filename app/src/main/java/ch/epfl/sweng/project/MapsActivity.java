package ch.epfl.sweng.project;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

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
    //list of photoObject and their marker shown on map
    private List<Marker> listMarker= new ArrayList<>();
    private List<PhotoObject> listPhoto;

    private GoogleMap mMap;

    private View mView;

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
        displayDBMarkers();
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
     * use our local database to display our markers on the map
     */
    public void displayDBMarkers()
    {
        listPhoto = new ArrayList<>(LocalDatabase.getMap().values());
        if(mMap!=null && mPhoneLatLng!=null) {
            //empty the map of the markers
            for(Marker marker:listMarker) {
            marker.remove();
            }
            refreshMapLocation(mPhoneLatLng);
            listMarker = new ArrayList<>();
            //add the new markers on the map
            for (PhotoObject photo : listPhoto) {
                boolean canActivateIt = photo.isInPictureCircle(mPhoneLatLng);
                LatLng photoPosition = new LatLng(photo.getLatitude(),photo.getLongitude());
                Marker photoMarker;
                //add a red marker if the photo can be seen
                if(canActivateIt) {
                    photoMarker = mMap.addMarker(new MarkerOptions()
                            .position(photoPosition)
                            .title(photo.getPhotoName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
                //add a yellow marker if it can't be activated to see the picture
                else{
                    photoMarker = mMap.addMarker(new MarkerOptions()
                            .position(photoPosition)
                            .title(photo.getPhotoName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                }
                //add the picture Id of the photo as custom object of the marker
                //useful to retrieve the picture
                photoMarker.setTag(photo.getPictureId());
                //add the marker in the list
                listMarker.add(photoMarker);
            }
        }
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
                    mMap.setInfoWindowAdapter(new PhotoOnMarker(this.getContext(), obj.getFullSizeImage()));
                }
            }
        }
    }
}
