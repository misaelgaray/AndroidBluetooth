package com.example.bluetooth.discoverdevices;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * TODO:We create a device adapter which simply is a layout for every list item.
 * Created by misaelgaray on 13/04/18.
 */

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    /*
    * The layout inflater instantiates the XML UI file where the design of our list items are
    * */
    private LayoutInflater inflater;
    /*
    * Saves the refence to the devices we find. That devices are passed once we instantiate this classes
    * */
    private ArrayList<BluetoothDevice> devices;
    /*
    * The id of de UI component that represents our list items
    * */
    private  int id;


    /*
    * Constructor where we pass the found devices, the id and the context
    * */
    public DeviceListAdapter(@NonNull Context context, int id, ArrayList<BluetoothDevice> devices) {
        super(context, id, devices);
        this.devices = devices;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.id = id;
    }

    /*
    * This method is called for every list(UI) item with the index of that item
    * We found the device that matches with the item position index in the devices
    * array and then show we show the information in list item.
    * */
    public View getView(int position, View convertView, ViewGroup parent){
        convertView = inflater.inflate(id, null);

        //Found the device in the array that matches with the curren item postition in the list(UI)
        BluetoothDevice device = devices.get(position);


        if(device != null){
            TextView name = (TextView) convertView.findViewById(R.id.deviceName);
            TextView address = (TextView) convertView.findViewById(R.id.deviceAddress);

            if(name != null)
                name.setText(device.getName());
            if(address != null)
                address.setText(device.getAddress());
        }

        return  convertView;
    }
}
