package com.example.bluetooth.enablebluetooth;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button enableBtn;
    public static final String TAG = "MainActivity";
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Here we call the button we declared in the UI by its id*/
        enableBtn = (Button) findViewById(R.id.enableBtn);

        //Initialize the Bluetooth adapter
    }
}
