package com.example.bluetooth.enablebluetooth;

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
import android.widget.TextView;

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
    TextView resultsView;


    /**
     * TODO: Creting a BluetoothAdapter variable
     * BluetoohAdapter class is the starter point to perform
     * bluetooth tasks like find devices and others related to
     * sync with devices.
     * */
    BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Here we call the button we declared in the UI by its id */
        enableBtn = (Button) findViewById(R.id.enableBtn);
        resultsView = (TextView) findViewById(R.id.resultsView);

        //TODO: Initialize the Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        //Calls the EnableDisableBluetooth method every time we click the button
        enableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnableDisableBluetooth();
            }
        });
    }


    /*TODO: Create a broadcas receiver
    * The broadcast will be notified when the bluetooth status changes and will
    * perform some tasks we wil define.
    * */
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                /*
                * We check what the current bluetooth status is.
                * We evaluate if in the switch statement below.
                * */
                switch (state){
                    case BluetoothAdapter.STATE_OFF :
                        resultsView.setText("State off");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF :
                        resultsView.setText("State turning off");
                        break;
                    case  BluetoothAdapter.STATE_ON :
                        resultsView.setText("State on");
                        break;
                    case  BluetoothAdapter.STATE_TURNING_ON:
                        resultsView.setText("State turnning on");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    //TODO: We create a function to enable or disable the Bluetooth
    public void EnableDisableBluetooth(){
        String message = "";

        /*The BluetoothAdapter couldn't find any bluetooth in the
        * device and no one instance was created
        * */
        if(mBluetoothAdapter == null){
            message = "This device has not bluetooth capabilities";
            resultsView.setText(message);
            return;
        }

        if(!mBluetoothAdapter.isEnabled()){
            message = "The bluetooth was enable and now is disabled.";
            mBluetoothAdapter.disable();
            resultsView.setText(message);
            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver, intentFilter);
            return;
        }

        if(mBluetoothAdapter.isEnabled()) {
            message = "The bluetooth was disable and now is enable";
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableIntent);
            resultsView.setText(message);
            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver, intentFilter);
           return;
        }

    }

}
