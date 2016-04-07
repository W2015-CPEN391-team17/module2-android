package ca.ubc.cpen391team17;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Data transfer to a geocache using Bluetooth.
 */
public class BluetoothActivity extends AppCompatActivity {
    // A constant that we use to determine if our request to turn on bluetooth worked
    private final static int REQUEST_ENABLE_BT = 1;
    // A handle to the tablet’s bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    private Context context;

    // App-defined int constant used for handling permissions requests
    private static final int BL_PERMISSIONS_REQUEST_BLUETOOTH = 0;
    private static final int BL_PERMISSIONS_REQUEST_BLUETOOTH_ADMIN = 1;

    private BluetoothArrayAdaptor PairedArrayAdapter;
    private BluetoothArrayAdaptor DiscoveredArrayAdapter;

    private BroadcastReceiver mReceiver ;

    // Store discovered devices
    private ArrayList <BluetoothDevice> DiscoveredDevices = new ArrayList <BluetoothDevice> ( ) ;
    private ArrayList< String > DiscoveredDetails = new ArrayList < String > ( ) ;

    // Store paired devices
    private ArrayList < BluetoothDevice > PairedDevices = new ArrayList < BluetoothDevice > ( ) ;
    private ArrayList < String > PairedDetails = new ArrayList < String > ( ) ;

    // Socket and streams to communicate
    private BluetoothSocket mmSocket = null;
    public static InputStream mmInStream = null;
    public static OutputStream mmOutStream = null;

    // Connection flag
    boolean connected = false;

    // maximum number of points that the DE2 component can handle
    public static final int MAX_LOCATIONS_SIZE = 1000;

    // list of locations that we get from the previous activity
    public List<Location> locations;

    private AdapterView.OnItemClickListener mPairedClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            String text = "Connecting to: " +
                    PairedDetails.get ( position );
            //Toast.makeText(context, text, Toast.LENGTH_LONG).show();

            if (connected) closeConnection(); // Disconnect before connecting again

            // Connect to the geocache
            CreateSerialBluetoothDeviceSocket(PairedDevices.get(position)) ;
            ConnectToSerialBlueToothDevice(); // user defined fn

            // update the view of discovered devices if required
            PairedArrayAdapter.notifyDataSetChanged();
        }
    };

    private AdapterView.OnItemClickListener mDiscoveredClickedHandler = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> parent, View v, int position, long id)
        {
            String text = "Connecting to: " +
                    DiscoveredDetails.get ( position );
            //Toast.makeText(context, text, Toast.LENGTH_LONG).show();

            if (connected) closeConnection(); // Disconnect before connecting again
            
            // Connect to the geocache
            CreateSerialBluetoothDeviceSocket(DiscoveredDevices.get(position)) ;
            ConnectToSerialBlueToothDevice();

            DiscoveredArrayAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        context = getApplicationContext();

        locations = (ArrayList<Location>) getIntent().getSerializableExtra("location_list");
        System.out.println(locations);

        // Check if the user has set the necessary permissions to use Bluetooth.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted; just exit the app for now
            Toast.makeText(context, "Please enable Bluetooth permissions",
                    Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission has not been granted; just exit the app for now
                Toast.makeText(context, "Please enable Bluetooth permissions",
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                // This call returns a handle to the one bluetooth device within your Android device
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
        }

        // Do we have a bluetooth device?
        if (mBluetoothAdapter == null) {
            Toast toast = Toast.makeText(context, "No Bluetooth adapter found", Toast.LENGTH_LONG);
            toast.show();
            finish();
            return ;
        }

        // Create adapters
        PairedArrayAdapter = new BluetoothArrayAdaptor(this,
                android.R.layout.simple_list_item_1, PairedDetails);
        DiscoveredArrayAdapter = new BluetoothArrayAdaptor(this,
                android.R.layout.simple_list_item_1, DiscoveredDetails);

        // Create views for bluetooth devices
        ListView PairedlistView = (ListView) findViewById( R.id.listView2 );
        ListView DiscoveredlistView = (ListView) findViewById( R.id.listView3 );
        PairedlistView.setOnItemClickListener (mPairedClickedHandler);
        DiscoveredlistView.setOnItemClickListener (mDiscoveredClickedHandler);
        PairedlistView.setAdapter (PairedArrayAdapter);
        DiscoveredlistView.setAdapter (DiscoveredArrayAdapter);

        // Enable bluetooth if necessary
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult (enableBtIntent, REQUEST_ENABLE_BT);
        }

        mReceiver = new BroadcastReceiver() {
            public void onReceive (Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice newDevice;

                if ( action.equals(BluetoothDevice.ACTION_FOUND) ) {
                    newDevice = intent.getParcelableExtra ( BluetoothDevice.EXTRA_DEVICE );

                    // Show details of found device
                    String theDevice = new String( newDevice.getName() +
                            "\nMAC Address = " + newDevice.getAddress());

//                    Toast.makeText(context, theDevice, Toast.LENGTH_LONG).show();

                    // Add to devices
                    DiscoveredDevices.add(newDevice);
                    DiscoveredDetails.add(theDevice);
                    DiscoveredArrayAdapter.notifyDataSetChanged();
                }
                else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    //Toast.makeText(context, "Discovery Started", Toast.LENGTH_LONG).show();
                }
                else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) ) {
                    //Toast.makeText(context, "Discovery Finished", Toast.LENGTH_LONG).show();
                }
            }
        };

        // Create receivers
        IntentFilter filterFound = new IntentFilter (BluetoothDevice.ACTION_FOUND);
        IntentFilter filterStart = new IntentFilter (BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filterStop = new IntentFilter (BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver (mReceiver, filterFound);
        registerReceiver (mReceiver, filterStart);
        registerReceiver (mReceiver, filterStop);

        Set< BluetoothDevice > thePairedDevices = mBluetoothAdapter.getBondedDevices();
        if (thePairedDevices.size() > 0) {
            Iterator<BluetoothDevice> iter = thePairedDevices.iterator() ;
            BluetoothDevice aNewdevice ;
            while ( iter.hasNext() ) {
                aNewdevice = iter.next();
                // Show details of paired device
                String PairedDevice = new String( aNewdevice.getName ()
                        + "\nMAC Address = " + aNewdevice.getAddress ());
                // Add to devices
                PairedDevices.add(aNewdevice);
                PairedDetails.add(PairedDevice);
                PairedArrayAdapter.notifyDataSetChanged();
            }
        }

        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery() ;
    }

    public void onDestroy() {
        unregisterReceiver ( mReceiver );
        super.onDestroy();
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        if( requestCode == REQUEST_ENABLE_BT) {
            if( resultCode != RESULT_OK ) {
                Toast toast = Toast.makeText(context, "Bluetooth failed to start",
                        Toast.LENGTH_LONG);
                toast.show();
                finish();
                return;
            }
        }
    }

    void closeConnection() {
        try {
            mmInStream.close();
            mmInStream = null;
        } catch (IOException e) {}
        try {
            mmOutStream.close();
            mmOutStream = null;
        } catch (IOException e) {}
        try {
            mmSocket.close();
            mmSocket = null;
        } catch (IOException e) {}
    }

    public void CreateSerialBluetoothDeviceSocket(BluetoothDevice device)
    {
        mmSocket = null;

        // UUID
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        try {
            mmSocket = device.createRfcommSocketToServiceRecord (MY_UUID);
        }
        catch (IOException e) {
            Toast.makeText(context, "Socket Creation Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void ConnectToSerialBlueToothDevice() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();
        try {
            mmSocket.connect();
            System.out.println("Connected");
            //Toast.makeText(context, "Connection Made", Toast.LENGTH_LONG).show();
            connected = true;
            CommunicateWithDE2();
        }
        catch (IOException connectException) {
            Toast.makeText(context, "Connection Failed", Toast.LENGTH_LONG).show();
            return;
        }

    }

    // Gets IO streams and sends data back and forth
    public void CommunicateWithDE2() {
        final int mID = 1;
        final int mID2 = 2;

        try {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
        } catch (IOException e) {
        System.out.println("Failed sockets");}

        System.out.println("Not blocking");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_upload)
                        .setContentTitle("Ready for Upload")
                        .setContentText("You are now sending to the geocache.");

        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        if(Build.VERSION.SDK_INT >= 21) mBuilder.setVibrate(new long[0]);
        
        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mID, mBuilder.build());

        Thread workerThread = new Thread(new Runnable() {
            public void run() {
                String latLongs;
                do {
                    latLongs = ReadFromBTDevice();
                } while (!latLongs.contains("#"));

                while (!latLongs.contains("?")) {
                    latLongs += ReadFromBTDevice();
                }

                mNotificationManager.cancel(mID);

                latLongs = latLongs.substring(latLongs.indexOf('#') + 1, latLongs.indexOf('?'));

                float lat;
                float lon;
                float latrange;
                float lonrange;

                String[] string_data = latLongs.split(",");

                lat = Float.valueOf(string_data[0]);
                lon = Float.valueOf(string_data[1]);
                latrange = Float.valueOf(string_data[2]);
                lonrange = Float.valueOf(string_data[3]);

                locations = trimLocations(locations, lat, lon, latrange, lonrange);


                String str = generateLocationsString(locations);
            do {
                WriteToBTDevice(str);
            }while(!ReadFromBTDevice().contains("="));
            }});

        workerThread.start();
        try {
            workerThread.join();
        }catch (InterruptedException e){
            System.out.println(e.toString());
        }

        closeConnection(); // Disconnect after writing

    }

    /**
     * Generate and return a string representation of locations
     * In this format
     * #0.000,0.000;0.000;0.000;0.000;0.000;?
     * which can be interpreted as
     * #lat1,lon1;lat2,lon2;lat3,lon3;?
     */
    public static String generateLocationsString(List<Location> locationsList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("#");
        for(Location location : locationsList) {
            stringBuilder.append(location.getLatitude());
            stringBuilder.append(",");
            stringBuilder.append(location.getLongitude());
            stringBuilder.append(";");
        }
        stringBuilder.append("?");
        return stringBuilder.toString();
    }

    /**
     * Remove any points from the locations ArrayList that appear before the last time that the path
     * crosses the edge of the screen. Also, the returned list will have the last MAX_LOCATIONS_SIZE
     * entries from the input if the input list has more than MAX_LOCATIONS_SIZE entries.
     * @param locationsList input list
     * @param lat the latitude of the DE2's location
     * @param lon the longitude of the DE2's location
     * @param latrange the onscreen latitude range from the DE2's location
     * @param lonrange the onscreen longitude range from the DE2's location
     */
    public static List<Location> trimLocations(List<Location> locationsList, float lat, float lon,
                                               float latrange, float lonrange) {
        // minimum and maximum latitudes and longitudes that will appear on the Nios screen
        float minLat = lat - latrange;
        float maxLat = lat + latrange;
        float minLon = lon - lonrange;
        float maxLon = lon + lonrange;

        // index in locations of the first location that will be retained
        int startingLocationsIndex = 0;

        // loop through each location in locations in reverse order
        // to determine the value of startingLocationsIndex
        for(int i = locationsList.size()-1; i >= 1; i--) {
            Location location = locationsList.get(i);
            if (location.getLatitude() < minLat || location.getLatitude() > maxLat ||
                    location.getLongitude() < minLon || location.getLongitude() > maxLon) {
                startingLocationsIndex = i+1; //the previous one we accessed
            }
        }

        // remove all entries that were originally before that index
        for(int i = 0; i < startingLocationsIndex; i++) {
            locationsList.remove(0);
        }

        if (locationsList.size() > MAX_LOCATIONS_SIZE) {
            // only include last MAX_LOCATIONS_SIZE locations
            final int number_to_remove = locationsList.size() - MAX_LOCATIONS_SIZE;
            for(int i = 0; i < number_to_remove; i++) {
                locationsList.remove(0);
            }
        }
        return locationsList;
    }

    // Write to BT
    public void WriteToBTDevice (String message) {
        //String s = new String("\r\n") ;
        byte[] msgBuffer = message.getBytes();
        //byte[] newline = s.getBytes();

        try {
            mmOutStream.write(msgBuffer) ;
            //mmOutStream.write(newline) ;
            //mmOutStream.flush();
        } catch (IOException e) { System.out.println("Out failed");}
    }

    // Read from BT
    public String ReadFromBTDevice() {
        byte c;
        String s = new String("");

        try { // Read from the InputStream using polling and timeout
            for (int i = 0; i < 1000; i++) { // try to read for 2 seconds max
                SystemClock.sleep(10);
                if (mmInStream.available() > 0) {
                    if ((c = (byte) mmInStream.read()) != '\r') {// '\r' terminator
                        s += (char) c; // build up string 1 byte by byte
                    } else {
                        return s;
                    }
                }
            }
        } catch (IOException e) {
            return new String("-- No Response --");
        }
        return s;
    }

    // Called when the back button is pressed.
    @Override
    public void onBackPressed() {
        mReceiver.abortBroadcast();
        mBluetoothAdapter.cancelDiscovery();
        super.onBackPressed();
        finish();
    }
}
