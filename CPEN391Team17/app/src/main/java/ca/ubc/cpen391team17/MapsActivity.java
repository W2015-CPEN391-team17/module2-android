package ca.ubc.cpen391team17;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //name of map
    String mapName = "";

    // Define a tag used for debugging
    private static final String MA_TAG = "MapsActivity";
    // App-defined int constant used for handling permissions requests
    private static final int MA_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    // Holds the GPS locations for the path that the user takes to the geocache.
    // Get a reference to the application-wide AppData.
    private List<Location> mUserPathLocations =
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
    private TimerTask mTimerTask = new TimerTask() {
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
                //for (Location location : mUserPathLocations) {
                //    Log.v(MA_TAG, "mRunnable: " + location.toString());
                //}

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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private void checkGooglePlayServices() {
        // Check if the user has the latest latest Google Play Services, and if not, prompt the
        // user to update it.
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, resultCode, 0);
            if (dialog != null) {
                //This dialog will help the user update to the latest GooglePlayServices
                dialog.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGooglePlayServices();
    }

    /**
     * Load from the given filename and return that list
     *
     * @param name
     * @return
     */
    public List<Location> loadLocationsList(String name) {
        String filename = name + ".dat";
        System.out.println("loadLocationsList: filename is " + filename);
        List<Location> locationsList = new ArrayList<Location>();
        File locationsListFile = new File(this.getApplicationContext().getFilesDir(), filename);
        /* only try to read the data if the file already exists */
        if (!locationsListFile.exists()) {
            return locationsList;
        }
        try {
            /* load LocationListState from a file */
            FileInputStream fileInputStream = openFileInput(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            LocationListState state = (LocationListState) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();

            /* update the list */
            int size = state.size();
            for(int i = 0; i < size; i++) {
                locationsList.add(state.remove());
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("loadLocationsList: size of locationsList is " + locationsList.size());

        return locationsList;
    }

    /**
     * Save the locationsList to a file in internal storage with name + ".dat"
     * @param locationsList
     */
    public void saveLocationsList(List<Location> locationsList, String name) {
        System.out.println("saveLocationsList: size of locationsList is " + locationsList.size());

        // create a serializable object
        LocationListState state = new LocationListState();
        for(Location location : locationsList) {
            state.add(location);
        }

        // save that object to a file
        String filename = name + ".dat";
        System.out.println("saveLocationsList: filename is " + filename);

        try {
            File locationsListStateFile = new File(this.getApplicationContext().getFilesDir(),
                    filename);
            FileOutputStream fileOutputStream = new FileOutputStream(locationsListStateFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(state);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkGooglePlayServices();

        Intent intent = getIntent();
        mapName = intent.getStringExtra("mapName");

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
                //Log.v(MA_TAG, location.toString());
                mLastRecordedLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
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

        loadOrStartTimer();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void loadOrStartTimer() {
        // check if we have a file to read the location data from
        System.out.println("loadOrStartTimer: (class's) mapName is " + mapName);
        String filename = mapName + ".dat";
        File locationsListFile = new File(this.getApplicationContext().getFilesDir(), filename);
        if (locationsListFile.exists()) {
            System.out.println("\n\nlocations list file exists********************\n\n");
            this.mUserPathLocations.clear();
            this.mUserPathLocations.addAll(loadLocationsList(mapName));
            mTimer.scheduleAtFixedRate(mTimerTask, 0, M_TIMER_PERIOD);
        } else {
            System.out.println("\n\nlocations list file does not exist******************\n\n");
            // Initialize the timer if we did not load data from a file
            mTimer.scheduleAtFixedRate(mTimerTask, 0, M_TIMER_PERIOD);
        }
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

        // Add a marker in bc
        LatLng bc = new LatLng(50, -123);
        mMarker = mMap.addMarker(new MarkerOptions().position(bc).title("User's Last Location"));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(bc)
                        .zoom(17)
                        .build()));
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
                Intent intent = new Intent(this, BluetoothActivity.class);
                ArrayList<Location> locations = new ArrayList<Location>();
                locations.addAll(mUserPathLocations);
                intent.putExtra("location_list", locations);
                startActivity(intent);
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
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(newMarkerPosition)
                                    .zoom(17)
                                    .build()));
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
                        loadOrStartTimer();
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

    @Override
    public void onStop() {
        super.onStop();

        saveLocationsList(this.mUserPathLocations, mapName);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ca.ubc.cpen391team17/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);

        mTimerTask.cancel();
        mTimer.purge();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public void onRestart() {
        super.onRestart();

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                updateTrackingPath();
            }
        };

       loadOrStartTimer();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ca.ubc.cpen391team17/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }
}
