package com.example.dticgeneral.sendingreceivingdata;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.nio.charset.Charset;
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


    //Create a accept thread
    private AcceptThread insequreAcceptThread;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothDevice bluetoothDevice;
    private UUID deviceUUID;
    ProgressDialog progressDialog;


    public BluetoothConnectionService(Context context) {

        this.context = context;
        //Getting the default addapte
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
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
                connected(socket, bluetoothDevice);
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


    /**
     * TODO: Create connect class
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket socket;

        public ConnectThread(BluetoothDevice device, UUID uuid){
            bluetoothDevice = device;
            deviceUUID = uuid;
        }

        public void run(){
            BluetoothSocket temp = null;
            Log.d(TAG, "run: CionnectThread running");

            /*
             * Get a bluetoothSocket for a connection with the given
             * bluetooth device
             * */
            try {
                Log.d(TAG, "ConnectThread: Trying  to create a InsecureRFcommSocket ussing UUID " + deviceUUID);
                temp = bluetoothDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.d(TAG, "run: " + e.getLocalizedMessage());
            }

            socket = temp;

            //Cancel bluetooth discovery because is too hardcore for the memory
            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
                Log.d(TAG, "run: Connected socket successfully");
            } catch (IOException e) {
                try {
                    socket.close();
                    Log.d(TAG, "run: The socket was closed ");
                } catch (IOException e1) {
                    Log.d(TAG, "run: " + e.getLocalizedMessage());
                }
            }

            connected(socket, bluetoothDevice);
        }

        //TODO: Create a method to close the server socket
        public void cancel() {
            Log.d(TAG, "cancel: Closing client socket");
            try {
                socket.close();
                Log.d(TAG, "cancel: Succesfully closed client socket");
            } catch (IOException ex) {
                Log.d(TAG, "cancel:" + ex.getLocalizedMessage());
            }
        }

    }


    /**
     * TODO: Start the service
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(TAG, "star");

        //Check if the connect thread is used, if so, let'w create a new one
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }

        if(insequreAcceptThread == null){
            insequreAcceptThread = new AcceptThread();
            insequreAcceptThread.start();
        }
    }

    /*
    * Accept thread starts and sits waoting for a connection
    * Then connectThread starts and attempts to make a connection with the other devices AcceptThread
    * */

    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient started");

        progressDialog = ProgressDialog.show(context, "Connecting Bluetooth", "Plizz wait", true);
        connectThread = new ConnectThread(device, uuid);
        connectThread.start();
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            Log.d(TAG, "ConnectedThread: Starting");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try{
                progressDialog.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created" + e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            //Keep listening to the input stream until an exception occurs.
            while (true){
                //Reads the input stream
                try{
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStrem: " + incomingMessage);
                }catch (IOException e){
                    Log.e(TAG, "Connected Thread " + e);
                    break;
                }
            }
        }

        //Sends data to the remote remote device
        public void write(byte [] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "Writing to output stream " + text);
            try{
                mmOutStream.write(bytes);
            }catch (IOException e){
                Log.e(TAG, "Output stream write failed  " + e);
            }
        }

        //Call this from the main activity to shutdown the connection
        public void cancel(){
            try {
                mmSocket.close();
            }catch (IOException e){
                Log.e(TAG, "ConnectedThread: Attempt to cancel failed  " + e);
            }
        }
    }

    public void connected(BluetoothSocket socket, BluetoothDevice device){
        Log.d(TAG, "connected: Starting connected method");

        //Start the thread to manage the connection and perform transmitions
        connectedThread = new ConnectedThread(socket);
        connectThread.start();
    }


    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        //TEmporary object
        ConnectThread r;

        //Sync a copy of the connected thread
        Log.d(TAG, "write: Write called");
        //Perform the write
        connectedThread.write(out);
    }
}























