package com.example.bluetooth.discoverdevices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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


    /**
     * TODO: Creting a BluetoothAdapter variable
     * BluetoohAdapter class is the starter point to perform
     * bluetooth tasks like find devices and others related to
     * sync with devices.
     * */
    BluetoothAdapter mBluetoothAdapter;

    /**
     * TODO: Create a Array that will contains all found devices
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

        //TODO: Initialize the Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //TODO: Initialize the bluetooth and make de device visible by default
        EnableDisableBluetooth();
        MakeDeviceVisibleAndConnectable();

        //Calls the EnableDisableBluetooth method every time we click the button
        enableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DiscoverDevices();
            }
        });
    }


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
    }

    /*
    * TODO: Create a method to discover the available devices
    * */
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
}
