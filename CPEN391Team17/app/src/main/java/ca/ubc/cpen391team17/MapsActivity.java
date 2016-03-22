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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean mIsTracking;
    private Polyline mTrackingPath;

    // Define a tag used for debugging
    private static final String TAG = "MapsActivity";
    // App-defined int constant used for handling permissions requests
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Create the GPS floating action button
        FloatingActionButton fab = new FloatingActionButton(this);
        fab.show();

        // On startup we are not tracking the GPS position of the user
        mIsTracking = false;
        mTrackingPath = null;

        // As this app requires the accuracy of GPS and may be used in places
        // without a network provider, we only use the GPS provider and not
        // the network provider.

        // Start GPS locking on startup and ask the user
        // to turn the GPS on if it has been disabled. If the
        // user enables GPS, begin locking onto it immediately.

        // Acquire a reference to the system Location Manager
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                // Just log the string representation of the location for now
                Log.v(TAG, location.toString());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates.
        // First, we check if the user has set permission to use the GPS.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted; request the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);
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

    public void onFABPressed(View view) {
        if (!mIsTracking) {
            mIsTracking = true;
            // Add demo PolyLine path on the map
            mTrackingPath = mMap.addPolyline(new PolylineOptions().geodesic(true)
                    .color(Color.RED)
                    .add(new LatLng(-33.866, 151.195))  // Sydney
                    .add(new LatLng(-18.142, 178.431))  // Fiji
                    .add(new LatLng(21.291, -157.821))  // Hawaii
                    .add(new LatLng(37.423, -122.091))  // Mountain View
            );
        } else {
            mTrackingPath.remove();
            mIsTracking = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                } else {
                    // Permission denied; Disable the
                    // functionality that depends on this permission.

                    // We just finish the app for now
                    this.finishAffinity();
                }
                return;
            }
            // Other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
