package com.application.controller.bluetooth

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.application.controller.BluetoothConnectActivity
import com.application.controller.MainActivity
import com.google.android.material.snackbar.Snackbar

class BluetoothFragment : Fragment() {
    private var bluetoothViewModel: BluetoothViewModel?= null
    private var bluetoothAdapter: BluetoothAdapter? = null

    private lateinit var myDevicesListView: ListView
    private lateinit var otherDevicesListView: ListView
    private var enableBluetoothButton: Button? = null
    private lateinit var refreshMyDevicesButton: Button
    private lateinit var refreshOtherDevicesButton: Button

    private var myDevicesArrayAdapter: ArrayAdapter<String>? = null
    private var otherDevicesArrayAdapter: ArrayAdapter<String>? = null

    // Used to store discovered device MAC addresses to filter unnamed devices
    private val discoveredDeviceMacAddresses = HashSet<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        bluetoothViewModel =
            ViewModelProvider(this).get(BluetoothViewModel::class.java)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        val root: View = inflater.inflate(com.application.controller.R.layout.fragment_bluetooth, container, false)
        myDevicesListView = root.findViewById<ListView>(com.application.controller.R.id.myDevicesListView)
        otherDevicesListView = root.findViewById<ListView>(com.application.controller.R.id.otherDevicesListView)
     //   enableBluetoothButton = root.findViewById<Button>(com.application.controller.R.id.enableBluetoothButton)
        refreshMyDevicesButton = root.findViewById<Button>(com.application.controller.R.id.refreshMyDevicesButton)
        refreshOtherDevicesButton = root.findViewById<Button>(com.application.controller.R.id.refreshOtherDevicesButton)

        // Initialize my devices list
        myDevicesArrayAdapter = ArrayAdapter<String>(this.requireContext(), com.application.controller.R.layout.listitem_bluetoothdevice)
        myDevicesListView.setAdapter(myDevicesArrayAdapter)
        myDevicesListView.setOnItemClickListener(deviceListClickListener)
        refreshMyDevicesList()

        // Initialize other devices list
        otherDevicesArrayAdapter = ArrayAdapter<String>(this.requireContext(), com.application.controller.R.layout.listitem_bluetoothdevice)
        otherDevicesListView.setAdapter(otherDevicesArrayAdapter)
        otherDevicesListView.setOnItemClickListener(deviceListClickListener)
        refreshOtherDeviceList()
/**
        enableBluetoothButton.setOnClickListener(View.OnClickListener { view ->
            enableBluetoothDiscovery(
                view
            )
        })**/

        refreshMyDevicesButton.setOnClickListener(View.OnClickListener { refreshMyDevicesList() })

        refreshOtherDevicesButton.setOnClickListener(View.OnClickListener { refreshOtherDeviceList() })

        registerBluetoothBroadcastReceiver()

        return root
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////               Methods for UI             ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint("MissingPermission")
    private fun enableBluetoothDiscovery(view: View) {
        if (bluetoothAdapter!!.scanMode != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
            startActivity(discoverableIntent)
            Log.d(BLUETOOTH_FRAGMENT_TAG, "Enabled bluetooth discoverable")
        } else {
            Log.d(BLUETOOTH_FRAGMENT_TAG, "Bluetooth discoverable already enabled")
        }

        Snackbar.make(view, DEVICE_IS_DISCOVERABLE, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    @SuppressLint("MissingPermission")
    private fun refreshMyDevicesList() {
        myDevicesArrayAdapter!!.clear()

        for (bluetoothDevice in bluetoothAdapter!!.bondedDevices) {
            myDevicesArrayAdapter!!.add(getDeviceNameWithMacAddress(bluetoothDevice))
        }
    }

    @SuppressLint("MissingPermission")
    private fun refreshOtherDeviceList() {
        otherDevicesArrayAdapter!!.clear()

        // Cancel current discovery session (if any)
        if (bluetoothAdapter!!.isDiscovering) {
            bluetoothAdapter!!.cancelDiscovery()
        }

        // Start new discovery session
        checkAndRequestBluetoothPermissions()
        bluetoothAdapter!!.startDiscovery()
        requireActivity().registerReceiver(
            bluetoothBroadcastReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
    }

    private val deviceListClickListener =
        OnItemClickListener { adapterView, view, arg2, arg3 ->
            val deviceName = (view as TextView).text.toString()
            Snackbar.make(view, CONNECTING_TO_DEVICE + deviceName, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            // Get the device MAC address from device name (the last 17 chars)
            val macAddress = deviceName.substring(deviceName.length - 19, deviceName.length - 2)

            // Call BluetoothService to connect with the selected device
           if (BluetoothConnectActivity.bluetoothService?.connectToBluetoothDevice(macAddress) == true) {
                Snackbar.make(view, CONNECTED_TO_DEVICE + deviceName, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////       Methods for BroadcastReceiver      ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private fun registerBluetoothBroadcastReceiver() {
        requireActivity().registerReceiver(
            bluetoothBroadcastReceiver,
            IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        )
        requireActivity().registerReceiver(
            bluetoothBroadcastReceiver,
            IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        )
        requireActivity().registerReceiver(
            bluetoothBroadcastReceiver,
            IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
        )

        requireActivity().registerReceiver(
            bluetoothBroadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        )
        requireActivity().registerReceiver(
            bluetoothBroadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    private val bluetoothBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                Log.d(
                    BLUETOOTH_FRAGMENT_TAG,
                    "bluetoothBroadcastReceiver: BluetoothDevice.ACTION_FOUND"
                )
                // Add new found device to other devices list
                val bluetoothDevice =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val macAddress = bluetoothDevice!!.address
                if (bluetoothDevice.name != null && bluetoothDevice.name.isNotEmpty() && !discoveredDeviceMacAddresses.contains(
                        macAddress
                    )
                ) {
                    val newDevice = getDeviceNameWithMacAddress(bluetoothDevice)
                    otherDevicesArrayAdapter!!.add(newDevice)
                    discoveredDeviceMacAddresses.add(macAddress) // Add MAC address to the set
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                Log.d(
                    BLUETOOTH_FRAGMENT_TAG,
                    "bluetoothBroadcastReceiver: BluetoothDevice.ACTION_BOND_STATE_CHANGED"
                )
                refreshMyDevicesList()
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED == action) {
                Log.d(
                    BLUETOOTH_FRAGMENT_TAG,
                    "bluetoothBroadcastReceiver: BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED"
                )
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                Log.d(
                    BLUETOOTH_FRAGMENT_TAG,
                    "bluetoothBroadcastReceiver: BluetoothDevice.ACTION_ACL_DISCONNECTED"
                )
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                Log.d(
                    BLUETOOTH_FRAGMENT_TAG,
                    "bluetoothBroadcastReceiver: BluetoothAdapter.ACTION_DISCOVERY_FINISHED"
                )
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                Log.d(
                    BLUETOOTH_FRAGMENT_TAG,
                    "bluetoothBroadcastReceiver: BluetoothAdapter.ACTION_STATE_CHANGED"
                )
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Helper Methods              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private fun checkAndRequestBluetoothPermissions() {
        val permissionCheck = (ActivityCompat.checkSelfPermission(
            requireContext(),
            "Manifest.permission.ACCESS_FINE_LOCATION"
        )
                + ActivityCompat.checkSelfPermission(
            requireContext(),
            "Manifest.permission.ACCESS_COARSE_LOCATION"
        ))
        if (permissionCheck != 0) {
            this.requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1001
            )
        } else {
            Log.d(BLUETOOTH_FRAGMENT_TAG, "Bluetooth permissions already ")
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceNameWithMacAddress(bluetoothDevice: BluetoothDevice?): String {
        return if (bluetoothDevice!!.name == null) {
            """Unnamed Device
| MAC Address: ${bluetoothDevice.address} |"""
        } else {
            """${bluetoothDevice.name}
| MAC Address: ${bluetoothDevice.address} |"""
        }
    }

    companion object {
        lateinit var bluetoothService: BluetoothService
        private const val BLUETOOTH_FRAGMENT_TAG = "BluetoothFragment"

        // Snackbar messages
        private const val CONNECTING_TO_DEVICE = "Connecting to "
        private const val CONNECTED_TO_DEVICE = "Connected to "
        private const val UNABLE_TO_CONNECT_TO_DEVICE = "Unable to connect to "
        private const val DEVICE_IS_DISCOVERABLE = "Your device is now discoverable via Bluetooth"
    }
}