package com.example.justin.bluetoothtest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Context context;
    // Bluetooth parameters
    private final static int REQUEST_ENABLE_BT = 1;
    private final static String DONGLENAME = "T17-GEOCACHE";
    private final static String DONGLEPASS = "1717";
    private BluetoothAdapter mBluetoothAdapter;
    // Broadcast receiver stuff
    private BroadcastReceiver mReceiver;
    private ArrayList<BluetoothDevice> DiscoveredDevices = new ArrayList<BluetoothDevice>();
    private ArrayList<String> DDStrings = new ArrayList<String>();
    // Socket and IO
    private BluetoothSocket mmSocket = null;
    public static InputStream mmInStream = null;
    public static OutputStream mmOutStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Do Bluetooth stuff with this fab for now
                context = getApplicationContext();
                // Enable adapter
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast toast = Toast.makeText(context, "THERE'S NO BLUETOOTH ADAPTER HERE DAWG", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                // Broadcast receiver
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
                            DiscoveredDevices.add ( newDevice );
                            DDStrings.add(theDevice);
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
                IntentFilter filterStop = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                // register our broadcast receiver so it gets called every time
                // a new bluetooth device is found or discovery starts or finishes
                // we should unregister it again when the app ends in onDestroy() - see later
                registerReceiver (mReceiver, filterFound);
                registerReceiver (mReceiver, filterStart);
                registerReceiver (mReceiver, filterStop);
                // Connect to the geocache device
                BluetoothDevice geocache = null;
                for (int i = 0; i < DiscoveredDevices.size(); i++) {
                    if (DiscoveredDevices.get(i).getName() == DONGLENAME) {
                        geocache = DiscoveredDevices.get(i);
                        break;
                    }
                }
                mmSocket = null;
                UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                try {
                    mmSocket = geocache.createRfcommSocketToServiceRecord (MY_UUID);
                }
                catch (IOException e) {
                    Toast.makeText(context, "Socket Creation Failed", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    // Attempt connection to the device through the socket.
                    mmSocket.connect();
                    Toast.makeText(context, "Connection Made", Toast.LENGTH_LONG).show();
                }
                catch (IOException connectException) {
                    Toast.makeText(context, "Connection Failed", Toast.LENGTH_LONG).show();
                    return;
                }
                // Get input streams
                try {
                    mmInStream = mmSocket.getInputStream();
                    mmOutStream = mmSocket.getOutputStream();
                } catch (IOException e) {
                    Toast.makeText(context, "Getting streams failed", Toast.LENGTH_LONG).show();
                    return;
                }
                // Try writing "hello world" to bluetooth
                try {
                    mmOutStream.write("hello world\r\n".getBytes());
                } catch (IOException e) {
                    Toast.makeText(context, "Writing hello world failed", Toast.LENGTH_LONG).show();
                    return;
                }
            };
        });
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver); // make sure we unregister our broadcast receiver
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_OK) {
                Toast toast = Toast.makeText(context, "Bluetooth failed to start dawg", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
