package com.example.dticgeneral.sendingreceivingdata;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.UUID;

//TODO: Implements the OnItemClickListener to check the selected item from the list
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /*
     * Create a tag to use it on the Logs
     * */
    public static final String TAG = "MainActivity";

    /*
     * We just declare a button variable to save the reference from
     * the button we created in the UI.
     * */
    Button enableBtn;
    /*
     * Declare a TextView component to write the results, this variable
     * saves the reference from the UI
     * */
    TextView resultsText;
    /*
    * This buttons is to stablish the connection between devices
    * */
    Button connectoBtn;
    /*
    * Button to send the write message
    * */
    EditText messageInput;
    /*Button to send the written message*/
    Button sendBtn;


    //TODO:
    private  static final UUID INSECURE_ID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    BluetoothDevice bluetoothDevice;

    /**
     * Creting a BluetoothAdapter variable
     * BluetoohAdapter class is the starter point to perform
     * bluetooth tasks like find devices and others related to
     * sync with devices.
     * */
    BluetoothAdapter mBluetoothAdapter;

    /**
     * Create a Array that will contains all found devices
     * */
    ArrayList<BluetoothDevice> devices = new ArrayList<>();

    /*
     * Reference to our list UI component
     * */
    DeviceListAdapter adapter;
    ListView devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Here we call the button we declared in the UI by its id */
        enableBtn = (Button) findViewById(R.id.enableBtn);
        devicesList = (ListView) findViewById(R.id.devicesList);
        resultsText = (TextView) findViewById(R.id.resultsText);
        connectoBtn = (Button) findViewById(R.id.connectBtn);
        messageInput = (EditText) findViewById(R.id.messageInput);
        sendBtn = (Button) findViewById(R.id.sendBtn);

        //Initialize the Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Initialize the bluetooth and make de device visible by default
        EnableDisableBluetooth();
        MakeDeviceVisibleAndConnectable();


        //TODO: Call a broadcast when the bond state of a remote device change
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBondBroadcastReceiver, filter);

        //TODO:Adding click item listener feature to the list of devices
        devicesList.setOnItemClickListener(MainActivity.this);


        //Calls the EnableDisableBluetooth method every time we click the button
        enableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiscoverDevices();
            }
        });
    }

    /*
    * TODO: Method that starts bluetooth connetions
    * */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        resultsText.setText("startBTConnections: Initializinf RFCOM BLuetooth connection,");

        
    }

    private  final BroadcastReceiver mBondBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                    resultsText.setText("THE DEVICE IS BOUNDED");
                }
                if(device.getBondState() == BluetoothDevice.BOND_BONDING){
                    resultsText.setText("THE DEVICE IS BOUNDING");
                }
                if(device.getBondState() == BluetoothDevice.BOND_NONE){
                    resultsText.setText("THE DEVICE IS NOT BOUNDED");
                }
            }
        }
    };


    /*TODO: Create a broadcas receiver to detect devices
     * The broadcast will be notified when the bluetooth status changes and will
     * perform some tasks we wil define.
     * */
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //TODO: Setting the broadcast to listen when the scan mode change
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);

                adapter = new DeviceListAdapter(context, R.layout.device_adapter, devices);
                devicesList.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(mBondBroadcastReceiver);
    }

    public void DiscoverDevices(){
        if(mBluetoothAdapter.isDiscovering()){
            Log.d(TAG, "Discovering mode disabled");
            mBluetoothAdapter.cancelDiscovery();

            mBluetoothAdapter.startDiscovery();

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver, filter);
        }else{
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver, filter);
        }
    }

    public void MakeDeviceVisibleAndConnectable(){
        //We call the service or component wich makes our device visible
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //Specify the visibility duration
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
    }


    public void EnableDisableBluetooth(){
        String message = "";

        /*The BluetoothAdapter couldn't find any bluetooth in the
         * device and no one instance was created
         * */
        if(mBluetoothAdapter == null){
            message = "This device has not bluetooth capabilities";
            return;
        }

        if(mBluetoothAdapter.isEnabled()){
            message = "The bluetooth is already enable";
            return;
        }

        if(!mBluetoothAdapter.isEnabled()) {
            message = "The bluetooth was disable and now is enable";
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
            return;
        }

    }



    //TODO: Override method that detects the selected item in the Devices list
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mBluetoothAdapter.cancelDiscovery();
        String name = devices.get(i).getName();
        String address = devices.get(i).getAddress();

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            resultsText.setText("Trying to connect with " + name);
            devices.get(i).createBond();
        }
    }
}