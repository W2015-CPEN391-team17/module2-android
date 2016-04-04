package ca.ubc.cpen391team17;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Define a tag used for debugging
    private static final String MA_TAG = "MapsActivity";
    // App-defined int constant used for handling permissions requests
    private static final int MA_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    // Holds the GPS locations for the path that the user takes to the geocache.
    // Get a reference to the application-wide AppData.
    private ArrayList<Location> mUserPathLocations =
            AppData.getInstance().getMapsActivityPathLocations();
    // GoogleMap field used by the app
    private GoogleMap mMap;
    // The visual representation of the user's path to the geocache
    private Polyline mTrackingPath;
    // A reference to the location manager of the app
    private LocationManager mLocationManager;
    // A reference to the location listener of the app
    private LocationListener mLocationListener;
    // The user's last recorded position
    private Location mLastRecordedLocation;
    // The map marker showing the user's last recorded position
    private Marker mMarker;
    // Period between executions for mTimer (in milliseconds)
    private final long M_TIMER_PERIOD = 5000;
    // Timer used to add the user's last recorded position to mUserPathLocations every so often
    private final Timer mTimer = new Timer();
    // TimerTask used by mTimer. Contains the actions to execute each time the timer is triggered.
    private final TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            updateTrackingPath();
        }
    };
    // Used to update the UI using the UI thread
    private final Handler mHandler = new Handler();
    // Runnable used by mHandler
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLastRecordedLocation != null) {
                // Record the user's last recorded location into mUserPathLocations
                Log.v(MA_TAG, "Pushing " +
                        mLastRecordedLocation.toString() + " into mLastRecordedLocation");
                mUserPathLocations.add(mLastRecordedLocation);
                for (Location location : mUserPathLocations) {
                    Log.v(MA_TAG, "mRunnable: " + location.toString());
                }


                // Dynamically update the on-screen PolyLine (the user's path to the geocache)
                if (mTrackingPath != null) {
                    mTrackingPath.remove();
                }
                // Update the on-screen path and move the marker.
                if (mMap != null) {
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
                    if (mMarker != null) {
                        mMarker.remove();
                    }
                    Location mostRecentLocation = mUserPathLocations.get(mUserPathLocations.size() - 1);
                    LatLng newMarkerPosition = new LatLng(mostRecentLocation.getLatitude(),
                            mostRecentLocation.getLongitude());
                    mMarker = mMap.addMarker(new MarkerOptions().position(newMarkerPosition)
                            .title("User's Last Location"));
                } else {
                    Log.w(MA_TAG, "mRunnable.run: mMap == null");
                }
            } else {
                Log.w(MA_TAG, "mRunnable.run: mLastRecordedLocation == null");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize and show the floating action button
        FloatingActionButton fab = new FloatingActionButton(this);
        fab.show();

        // Initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

                // Update the user's last recorded location
                Log.v(MA_TAG, location.toString());
                mLastRecordedLocation = location;
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
        if (mLocationManager == null) {
            // The app is essentially useless without being able to get the user's
            // location. For now, we just finish (quit) the app.
            this.finishAffinity();
        }
        // Permission has already been granted; start retrieving the user's location
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, mLocationListener);

        // Initialize the timer
        mTimer.scheduleAtFixedRate(mTimerTask, 0, M_TIMER_PERIOD);
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
        mMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("User's Last Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Show the toolbar menu items.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Called when an action bar item is pressed and performs the
     * actions corresponding to the item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_upload:
                // User chose the "Upload" item.
                // Start the Bluetooth Activity.
                startActivity(new Intent(this, BluetoothActivity.class));
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when the floating action button (FAB) is pressed.
     */
    public void onFABPressed(View view) {
        // Update the marker's location and move the marker, then move the camera to it
        if (mUserPathLocations != null) {
            if (!mUserPathLocations.isEmpty()) {
                Location mostRecentLocation = mUserPathLocations.get(mUserPathLocations.size() - 1);
                LatLng newMarkerPosition = new LatLng(mostRecentLocation.getLatitude(),
                        mostRecentLocation.getLongitude());
                if (mMap != null) {
                    if (mMarker != null) {
                        mMarker.remove();
                    }
                    mMarker = mMap.addMarker(new MarkerOptions().position(newMarkerPosition)
                            .title("User's Last Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(newMarkerPosition));
                } else {
                    Log.w(MA_TAG, "onFABPressed: mMap == null");
                }
            } else {
                Log.w(MA_TAG, "onFABPressed: mUserPathLocations.isEmpty() == true");
            }
        } else {
            Log.w(MA_TAG, "onFABPressed: mUserPathLocations == null");
        }
    }

    /**
     * Update the user's path to the geocache on the screen.
     */
    public void updateTrackingPath() {
        mHandler.post(mRunnable);
    }

    /**
     * Called when the user grants or does not grant an Android permission.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MA_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted; start retrieving the user's location
                    if (mLocationManager == null) {
                        // The app is essentially useless without being able to get the user's
                        // location. For now, we just finish (quit) the app.
                        this.finishAffinity();
                    }
                    try {
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                0, 0, mLocationListener);
                        // Initialize the timer
                        mTimer.scheduleAtFixedRate(mTimerTask, 0, M_TIMER_PERIOD);
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

    /**
     * Called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Finish the activity
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Manually destroy async objects
        mTimer.cancel();
        mTimer.purge();
        mHandler.removeCallbacks(mRunnable);
    }
}
