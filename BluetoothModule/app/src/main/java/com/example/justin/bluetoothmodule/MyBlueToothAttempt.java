package com.example.justin.bluetoothmodule;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class MyBlueToothAttempt extends ActionBarActivity
{
    // A constant that we use to determine if our request to turn on bluetooth worked
    private final static int REQUEST_ENABLE_BT = 1;
    // A handle to the tablet’s bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    // get the context for the application. We use this with things like "toast" popups
    private Context context;

    // two instances of our new custom array adaptor
    private MyCustomArrayAdaptor myPairedArrayAdapter;
    private MyCustomArrayAdaptor myDiscoveredArrayAdapter;

    private BroadcastReceiver mReceiver ; // handle to BroadCastReceiver object
    // an Array/List to hold discovered Bluetooth devices
    // A bluetooth device contains device Name and Mac Address information which
    // we want to display to the user in a List View so they can chose a device
    // to connect to. We also need that info to actually connect to the device
    private ArrayList < BluetoothDevice > Discovereddevices = new ArrayList <BluetoothDevice> ( ) ;
    // an Array/List to hold string details of the Bluetooth devices, name + MAC address etc.
    // this is displayed in the listview for the user to chose
    private ArrayList < String > myDiscoveredDevicesStringArray = new ArrayList < String > ( ) ;

    // we want to display all paired devices to the user in a ListView so they can chose a device
    private ArrayList < BluetoothDevice > Paireddevices =
            new ArrayList < BluetoothDevice > ( ) ;
    // an Array/List to hold string details of the Paired Bluetooth devices, name + MAC address etc.
    // this is displayed in the listview for the user to chose
    private ArrayList < String > myPairedDevicesStringArray =
            new ArrayList < String > ( ) ;

    // a “socket” to a blue tooth device
    private BluetoothSocket mmSocket = null;
    // input/output “streams” with which we can read and write to device
    // use of “static” important, it means variables can be accessed
    // without an object, this is useful as other activities can use
    // these streams to communicate after they have been opened.
    public static InputStream mmInStream = null;
    public static OutputStream mmOutStream = null;
    // indicates if we are connected to a device
    private boolean Connected = false;

    private AdapterView.OnItemClickListener mPairedClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            // position = row number that user touched
            // setValid(…) is a user written function in the custom array adaptor class
            myPairedArrayAdapter.setValid(position);
            // inform array adaptor that data has changed
            myPairedArrayAdapter.notifyDataSetChanged();
        }
    };

    private AdapterView.OnItemClickListener mDiscoveredClickedHandler = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> parent, View v, int position, long id)
        {
            // get the details of the device name etc.
            String text = "Discovered Device: " +
                    myDiscoveredDevicesStringArray.get ( position );
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();

            // we are going to connect to the other device as a client
            // if we are already connected to a device, close connections
            if(Connected == true)
                closeConnection(); // user defined fn to close streams and socket

            // get the selected bluetooth device based on position then connect to it
            // see page 24 and 25
            CreateSerialBluetoothDeviceSocket( Discovereddevices.get (position) ) ;
            ConnectToSerialBlueToothDevice(); // user defined fn

            // update the view of discovered devices if required
            myDiscoveredArrayAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView(R.layout.activity_my_blue_tooth_attempt);
        // get the context for the application
        context = getApplicationContext();

        // This call returns a handle to the one bluetooth device within your Android device
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // check to see if your android device even has a bluetooth device !!!!,
        if (mBluetoothAdapter == null) {
            Toast toast = Toast.makeText(context, "NO BLUETOOTH DAWG", Toast.LENGTH_LONG);
            toast.show();
            finish(); // if no bluetooth device on this tablet don’t go any further.
            return ;
        }

        // create the new adaptors passing important params, such
        // as context, android row style and the array of strings to display
        myPairedArrayAdapter = new MyCustomArrayAdaptor(this,
                android.R.layout.simple_list_item_1, myPairedDevicesStringArray);
        myDiscoveredArrayAdapter = new MyCustomArrayAdaptor(this,
                android.R.layout.simple_list_item_1, myDiscoveredDevicesStringArray);

        // get handles to the two list views in the Activity main layout
        ListView PairedlistView = (ListView) findViewById( R.id.listView2 );
        ListView DiscoveredlistView = (ListView) findViewById( R.id.listView3 );

        // add some action listeners for when user clicks on row in either list view
        PairedlistView.setOnItemClickListener (mPairedClickedHandler);
        DiscoveredlistView.setOnItemClickListener (mDiscoveredClickedHandler);

        // set the adaptor view for both list views above
        PairedlistView.setAdapter (myPairedArrayAdapter);
        DiscoveredlistView.setAdapter (myDiscoveredArrayAdapter);

        // If the bluetooth device is not enabled, let’s turn it on
        if (!mBluetoothAdapter.isEnabled()) {
            // create a new intent that will ask the bluetooth adaptor to “enable” itself.
            // A dialog box will appear asking if you want turn on the bluetooth device
            Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            // REQUEST_ENABLE_BT below is a constant (defined as '1 - but could be anything)
            // When the “activity” is run and finishes, Android will run your onActivityResult()
            // function (see next page) where you can determine if it was successful or not
            startActivityForResult (enableBtIntent, REQUEST_ENABLE_BT);
        }

        mReceiver = new BroadcastReceiver() {
            public void onReceive (Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice newDevice;

                if ( action.equals(BluetoothDevice.ACTION_FOUND) ) { // If a new BT device found
                    // Intent will contain discovered Bluetooth Device so go and get it
                    newDevice = intent.getParcelableExtra ( BluetoothDevice.EXTRA_DEVICE );

                    // Add the name and address to the custom array adapter to show in a ListView
                    String theDevice = new String( newDevice.getName() +
                            "\nMAC Address = " + newDevice.getAddress());

                    Toast.makeText(context, theDevice, Toast.LENGTH_LONG).show();

                    //add the new device and string details to the two arrays (page 15)
                    Discovereddevices.add ( newDevice );
                    myDiscoveredDevicesStringArray.add ( theDevice );

                    // notify array adaptor that the contents of String Array have changed
                    myDiscoveredArrayAdapter.notifyDataSetChanged ();
                }
                // visual feedback for user
                else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    Toast.makeText(context, "Discovery Started", Toast.LENGTH_LONG).show();
                }
                else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) ) {
                    Toast.makeText(context, "Discovery Finished", Toast.LENGTH_LONG).show();
                }
            }
        };

        // create 3 separate IntentFilters that are tuned to listen to certain Android broadcasts
        // 1) when new Bluetooth devices are discovered,
        // 2) when discovery of devices starts (not essential but give useful feedback)
        // 3) When discovery ends (not essential but give useful feedback)
        IntentFilter filterFound = new IntentFilter (BluetoothDevice.ACTION_FOUND);
        IntentFilter filterStart = new IntentFilter (BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filterStop = new IntentFilter (BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // register our broadcast receiver so it gets called every time
        // a new bluetooth device is found or discovery starts or finishes
        // we should unregister it again when the app ends in onDestroy() - see later
        registerReceiver (mReceiver, filterFound);
        registerReceiver (mReceiver, filterStart);
        registerReceiver (mReceiver, filterStop);

        Set< BluetoothDevice > thePairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are devices that have already been paired
        // get an iterator for the set of devices and iterate 1 device at a time
        if (thePairedDevices.size() > 0) {
            Iterator<BluetoothDevice> iter = thePairedDevices.iterator() ;
            BluetoothDevice aNewdevice ;
            while ( iter.hasNext() ) { // while at least one more device
                aNewdevice = iter.next(); // get next element in set
                // Add the name and address to an array adapter to show in a ListView
                String PairedDevice = new String( aNewdevice.getName ()
                        + "\nMAC Address = " + aNewdevice.getAddress ());

                //add the new device details to the array
                Paireddevices.add (aNewdevice);
                myPairedDevicesStringArray.add (PairedDevice);
                myPairedArrayAdapter.notifyDataSetChanged ();
            }
        }

        // Before starting discovery make sure discovery is cancelled
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        // now start scanning for new devices. The broadcast receiver
        // we wrote earlier will be called each time we discover a new device
        // don't make this call if you only want to show paired devices
        mBluetoothAdapter.startDiscovery() ;
    }

    public void onDestroy() {
        unregisterReceiver ( mReceiver ); // make sure we unregister
        // our broadcast receiver
        super.onDestroy();
    }

    // this call back function is run when an activity that returns a result ends
    // check the requestCode (given when we start the activity) to identify which
    // activity is returning a result, and then resultCode is the value returned
    // by the activity. In most cases this is RESULT_OK. If not end the activity
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

        Connected = false ;
    }

    public void CreateSerialBluetoothDeviceSocket(BluetoothDevice device)
    {
        mmSocket = null;

        // universal UUID for a serial profile RFCOMM blue tooth device
        // this is just one of those “things” that you have to do and just works
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        // Get a Bluetooth Socket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
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
            // Attempt connection to the device through the socket.
            mmSocket.connect();
            Toast.makeText(context, "Connection Made", Toast.LENGTH_LONG).show();
        }
        catch (IOException connectException) {
            Toast.makeText(context, "Connection Failed", Toast.LENGTH_LONG).show();
            return;
        }

        //create the input/output stream and record fact we have made a connection
        GetInputOutputStreamsForSocket(); // see page 26
        Connected = true;
    }

    // gets the input/output stream associated with the current socket
    public void GetInputOutputStreamsForSocket() {
        try {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
        } catch (IOException e) { }
        // TEST ON CONNECT
        String foo = "";
        while(foo.equals("")) {
            foo = ReadFromBTDevice();
        }
        System.out.println(foo);
        String send = "";
        send += (char) 0x01;
        while(true) {
            WriteToBTDevice(send);
        }
    }

    // This function write a line of text (in the form of an array of bytes)
    // to the Bluetooth device and then sends the string “\r\n”
    // (required by the bluetooth dongle)
    public void WriteToBTDevice (String message) {
        String s = new String("\r\n") ;
        byte[] msgBuffer = message.getBytes();
        byte[] newline = s.getBytes();

        try {
            mmOutStream.write(msgBuffer) ;
            mmOutStream.write(newline) ;
        } catch (IOException e) { }
    }

    // This function reads a line of text from the Bluetooth device
    public String ReadFromBTDevice() {
        byte c;
        String s = new String("");

        try { // Read from the InputStream using polling and timeout
            for (int i = 0; i < 2000; i++) { // try to read for 2 seconds max
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
}
