package com.example.karibu;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends Activity  {
    private static final String TAG ="BT" ;
    Button turnOn,getVisible,listDevices,turnOff,btnDiscover;
    private Set<BluetoothDevice>pairedDevices;
    ListView lv;
     ArrayList<String> mDeviceList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
      BluetoothAdapter BA=BluetoothAdapter.getDefaultAdapter();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        turnOn= (Button) findViewById(R.id.btn1);
        getVisible=(Button)findViewById(R.id.button2);
        listDevices=(Button)findViewById(R.id.button3);
        turnOff=(Button)findViewById(R.id.button4);
        btnDiscover=(Button)findViewById(R.id.btnDiscover);
        lv = (ListView)findViewById(R.id.listView);

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Discovering Devices",Toast.LENGTH_SHORT).show();

                arrayAdapter.clear();
                checkBTPermissions();
                BA.startDiscovery();

            }
        });
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        //setting the adapters

        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mDeviceList);
        lv.setAdapter(arrayAdapter);

        //end of oncreate
    }
    BroadcastReceiver  receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device.getName());
                arrayAdapter.notifyDataSetChanged();


            }
            else{

                Toast.makeText(getApplicationContext(), "Oops No Discoverable devices at the moment",Toast.LENGTH_SHORT).show();
            }

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();


        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    public void on(View v){


        if (BA == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not supported",Toast.LENGTH_LONG).show();
        }

        else if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }


    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }




    public void list(View v){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkBTPermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}