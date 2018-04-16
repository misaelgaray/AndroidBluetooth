package com.example.dticgeneral.sendingreceivingdata;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

/**
 * TODO:Create a connection class
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */

public class BluetoothConnectionService {

    public static  final String TAG = "BluetoothConnectionS";

    //TODO: create a chat service name
    private static final  String appName = "MyApp";

    /*
    * TODO: UUID to communication between devices
    * this UUID is a class that generates uniques 128-bit identifiers. These will be used
    *  int the communication with other devices..
    * */
    private  static final UUID INSECURE_ID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");


    //TODO:Create a basic bluetooth adapter
    private final BluetoothAdapter bluetoothAdapter;

    //TODO: receive the context of the class instantiating this.
    Context context;

    private AcceptThread insequreAcceptThread;


    public BluetoothConnectionService(Context context) {

        this.context = context;
        //Getting the default addapte
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    /**
     * TODO: create a thread to listen requests
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;

        public  AcceptThread(){
            BluetoothServerSocket tmpServer = null;

            //Create a new listening server socket
            try{
                tmpServer = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, INSECURE_ID);

                Log.d(TAG, "AcceptThread : Setting up server using " + INSECURE_ID);
            }catch (IOException ex){
                Log.d(TAG, ex.getLocalizedMessage());
            }

            serverSocket = tmpServer;
        }

        //Run method of thread
        public void run(){
            BluetoothSocket socket = null;
            Log.d(TAG, "AcceptThread running  ");



            try {
                Log.d(TAG, "run: RFCOM server socket start...... ");
                socket = serverSocket.accept();
                Log.d(TAG, "run: RFCOM server socker accepted connection");
            } catch (IOException ex) {
                Log.d(TAG, "run: " + ex.getLocalizedMessage());
            }

            if(socket != null){
                connected(socket, device);
            }
        }

        //TODO: Create a method to close the server socket
        public void cancel() {
            Log.d(TAG, "cancel: Closing server socket");
            try {
                serverSocket.close();
                Log.d(TAG, "cancel: Succesfully closed server socket");
            } catch (IOException ex) {
                Log.d(TAG, "cancel:" + ex.getLocalizedMessage());
            }
        }
    }
}
