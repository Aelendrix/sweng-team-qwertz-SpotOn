package ch.epfl.sweng.project;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Location mPhoneLocation;

    private static final LatLng DEFAULT_LOCATION = new LatLng(50,10);
    //esplanade epfl (under one roof)
    private static final LatLng FAKE_SPOT_1 = new LatLng(46.519241, 6.565911);
    //moutons Unil
    private static final LatLng FAKE_SPOT_2 = new LatLng(46.521002, 6.575986);
    //centre sportif
    private static final LatLng FAKE_SPOT_3 = new LatLng(46.519403, 6.579841);
    //Flon
    private static final LatLng FAKE_SPOT_4 = new LatLng(46.520844, 6.630718);

    private Marker mLocationMarker;

    //useful later for marker triggering and pattern matching
    //private Marker mSpot1Marker;
    //private Marker mSpot2Marker;
    //private Marker mSpot3Marker;
    //private Marker mSpot4Marker;

    private GoogleMap mMap;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Acquire a reference to the system Location Manager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("location","location Changed");
                refreshLocation();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        final int TIME_BETWEEN_LOCALISATION = 60 * 1000; //1 Minutes
        final int MIN_DISTANCE_CHANGE_UPDATE = 10; // 1 Meter
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_BETWEEN_LOCALISATION, MIN_DISTANCE_CHANGE_UPDATE, locationListener);
    }

    //function called when the locationListener see a location change
    private void refreshLocation()
    {
        try {
            if (mLocationManager != null) {
                //check if gps is enable
                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //get the location according of the gps
                    mPhoneLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (mPhoneLocation != null) {
                        //now apply the location to the map
                        if (mMap != null) {
                            LatLng mPhoneLatLng = new LatLng(mPhoneLocation.getLatitude(), mPhoneLocation.getLongitude());
                            //change the localisation cursor, if null, create one instead
                            if (mLocationMarker == null) {
                                mLocationMarker = mMap.addMarker(new MarkerOptions()
                                        .position(mPhoneLatLng)
                                        .title("My position")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            } else {
                                mLocationMarker.setPosition(mPhoneLatLng);
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(mPhoneLatLng));

                        }
                    }
                }
            }
        }
        /*Catch exception because location acces always need to have the localisation permission
        * In our app if the permission is rejected, we can't access this activity anyway (ATM)
        */
        catch(SecurityException e) {
            e.printStackTrace();
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

        /*add the fake objects on our map
        *TODO: need to change this part when the DB will be implemented
        *strings are hardcoded because theses fake data will be stored in a DB and not in the strings.xml
        *for the demo, a simple position to test is (46.5,6.6)
        */
        //mSpot1Marker =
        mMap.addMarker(new MarkerOptions()
                .position(FAKE_SPOT_1)
                .title("Under one roof")
                .snippet("rip esplanade"));
        //mSpot2Marker
        mMap.addMarker(new MarkerOptions()
                .position(FAKE_SPOT_2)
                .title("Moutons")
                .snippet("xD sheep"));
        //mSpot3Marker =
        mMap.addMarker(new MarkerOptions()
                .position(FAKE_SPOT_3)
                .title("Centre Sportif")
                .snippet("i love sport"));
        //mSpot4Marker =
        mMap.addMarker(new MarkerOptions()
                .position(FAKE_SPOT_4)
                .title("Flon")
                .snippet("nice place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION,10.0f));
    }
}
