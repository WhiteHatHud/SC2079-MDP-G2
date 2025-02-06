package com.application.controller

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.application.controller.CommunicationActivity.Companion
import com.application.controller.bluetooth.BluetoothFragment
import com.application.controller.bluetooth.BluetoothService
import com.google.android.material.snackbar.Snackbar


class BluetoothConnectActivity : AppCompatActivity()
{
    @RequiresApi(Build.VERSION_CODES.S)
    private val bluetoothPermissions = arrayOf(
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_ADVERTISE,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATIO-N
    )
    private val REQUEST_BLUETOOTH_PERMISSIONS = 1
    private var bluetoothService: BluetoothService? = null
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_connect)
        val toCommsButton: Button = findViewById(R.id.button_fromBluetoothToCommunication)
        toCommsButton.setOnClickListener {
            val intent= Intent(this, CommunicationActivity::class.java)
            startActivity(intent)
        }
        requestBluetoothPermissions()
        BluetoothFragment.Companion.bluetoothService = BluetoothService()

        /**
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        var bluetoothWorking=false;
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth

            bluetoothWorking=false;
        }
        else
        {
            if (bluetoothAdapter.isEnabled) {
                // Bluetooth is already enabled
                Toast.makeText(this, "Bluetooth is already enabled", Toast.LENGTH_SHORT).show()
                bluetoothWorking=true;
            }
            else
            {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                startActivity(enableBtIntent, savedInstanceState);
                if(bluetoothAdapter.isEnabled)
                {
                    Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_SHORT).show()
                    bluetoothWorking=true;
                }
                else
                {
                    Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
                    bluetoothWorking=false;
                }
            }
        }
        if(bluetoothWorking)
        {
            //BLUETOOTH WORKS
        //Check if devices are already connected
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
            }
            if (pairedDevices != null) {
                for(device in pairedDevices) {
                    //Check if RPI or Car is already connected
                    //If it is, can move on and skip discovery
                }
            }
            //Begin doing Bluetooth connecting to device
            // Register for broadcasts when a device is discovered.
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(receiver, filter)
        }
    **/

    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothPermissions() {
        val permissionsToRequest = bluetoothPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, REQUEST_BLUETOOTH_PERMISSIONS)
        } else {
            // Permissions already granted, proceed with Bluetooth operations
           // startBluetoothOperations()
            bluetoothService = BluetoothService()
            if (bluetoothService!!.isConnectedToBluetoothDevice) {
                Toast.makeText(this, bluetoothService!!.connectedDeviceName, Toast.LENGTH_LONG).show();

                    supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        add(R.id.fragmentContainerView, BluetoothFragment())

                }

            } else {
                Toast.makeText(this, "NO DEVICE CONNECTED", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceHardwareAddress = device?.address // MAC address


                }
            }


        }

    }

    companion object {
        var bluetoothService: BluetoothService? = BluetoothService()
     /*
        fun getBluetoothService(): BluetoothService {
           // return CommunicationActivity.bluetoothService!!
            return BluetoothConnectActivity.bluetoothService!!
        }*/
    }


}
