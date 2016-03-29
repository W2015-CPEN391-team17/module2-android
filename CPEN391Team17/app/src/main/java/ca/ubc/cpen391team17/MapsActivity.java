package ca.ubc.cpen391team17;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // Define a tag used for debugging
    private static final String MA_TAG = "MapsActivity";
    // App-defined int constant used for handling permissions requests
    private static final int MA_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    // Holds the GPS locations for the path that the user takes to the geocache
    private ArrayList<Location> mUserPathLocations = new ArrayList<>();
    // GoogleMap field used by the app
    private GoogleMap mMap;
    // Flag indicating if the app is currently tracking the user's path the geocache.
    // On startup we are not tracking the GPS position of the user.
    private boolean mIsTracking = false;
    // The visual representation of the user's path to the geocache
    private Polyline mTrackingPath = null;
    // A reference to the floating action button of the app
    private FloatingActionButton mFAB = null;
    // A reference to the location manager of the app
    private LocationManager mLocationManager = null;
    // A reference to the location listener of the app
    private LocationListener mLocationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize and show the floating action button
        mFAB = new FloatingActionButton(this);
        mFAB.show();

        // As this app requires the accuracy of GPS and may be used in places
        // without a network provider, we only use the GPS provider and not
        // the network provider.

        // Start GPS locking on startup and ask the user
        // to turn the GPS on if it has been disabled. If the
        // user enables GPS, begin locking onto it immediately.

        // Initialize the Location Manager
        mLocationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                // Push the location into mUserPathLocations for now
                Log.v(MA_TAG, location.toString());
                mUserPathLocations.add(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates.
        // First, we check if the user has set permission to use the GPS.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted; request the user to grant permission.
            // If permission will be granted, the onRequestPermissionsResult callback method
            // will start retrieving the user's location.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MA_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // Permission has already been granted; start retrieving the user's location
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, mLocationListener);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Called when the floating action button (FAB) is pressed.
     */
    public void onFABPressed(View view) {
        // Each press of the FAB toggles tracking on and off.
        if (!mIsTracking) {
            mIsTracking = true;
            // Convert the locations in mUserPathLocations to create a PolyLine path on the map
            ArrayList<LatLng> pathCoordinates = new ArrayList<>();
            for (Location location : mUserPathLocations) {
                pathCoordinates.add(new LatLng(location.getLatitude(), location.getLongitude()));
            }
            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.RED);
            for (LatLng latLng : pathCoordinates) {
                polylineOptions.add(latLng);
            }
            mTrackingPath = mMap.addPolyline(polylineOptions);
        } else {
            mTrackingPath.remove();
            mIsTracking = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MA_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted; start retrieving the user's location
                    try {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                0, 0, mLocationListener);
                    } catch (SecurityException e) {
                        // The app is essentially useless without being able to get the user's
                        // location. For now, we just finish (quit) the app.
                        this.finishAffinity();
                        // If for some reason the app hasn't finished by this point, we rethrow
                        // the exception.
                        throw e;
                    }
                } else {
                    // Permission denied; Disable the
                    // functionality that depends on this permission.

                    // The app is essentially useless without being able to get the user's
                    // location. For now, we just finish (quit) the app.
                    this.finishAffinity();
                }
            }
            // We may add other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
