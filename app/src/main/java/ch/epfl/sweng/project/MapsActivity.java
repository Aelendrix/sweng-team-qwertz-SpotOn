package ch.epfl.sweng.project;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends Fragment implements OnMapReadyCallback {


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

    private LatLng mPhoneLatLng;
    private Marker mLocationMarker;
    private List<Marker> listMarker= new ArrayList<>();
    private List<PhotoObject> listPhoto;

    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View mView =  inflater.inflate(R.layout.activity_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        return mView;


    }

    //function called when the locationListener (in tabActivity) see a location change
    public void refreshMapLocation(Location phoneLocation) {

        if (phoneLocation != null) {
            //now apply the location to the map
            if (mMap != null) {
                            mPhoneLatLng = new LatLng(phoneLocation.getLatitude(), phoneLocation.getLongitude());
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
        //default location in the Geneva Lake
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION,10.0f));
    }

    //use our local database to display our markers
    public void displayDBMarkers(LocalDatabase DB)
    {
        listPhoto = new ArrayList<>(DB.getMap().values());
        if(mMap!=null) {
            //empty the map of the markers
            for(Marker marker:listMarker) {
            marker.remove();
            }
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
}
