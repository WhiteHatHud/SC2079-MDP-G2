package com.application.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.application.controller.bluetooth.BluetoothService
import com.application.controller.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding
    private lateinit var BluetoothService : BluetoothService
    private var bluetoothConnectedFlag=false;
    private lateinit var BluetoothStatusButton:Button;
    /*
    TODO- Look into establishing access to the Bluetooth Service access in order to translate movement instructions
      from Frag1 to directly to Bluetooth Service - Companion to Bluetooth Activity? - Check
      if connected and then allow messages to be sent
     Done at activity to level to potentially convert Frag2 to maze interface.
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        BluetoothStatusButton=findViewById(R.id.button_menuActivityCheckBluetoothStatus)
        BluetoothStatusButton.setOnClickListener{ view ->
            Snackbar.make(view, "Currently Connected  to "+BluetoothService.connectedDeviceName, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()

        }
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "TODO Will open Bluetooth Menu", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()

            val intent= Intent(this, BluetoothConnectActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        checkBluetoothStatusChange()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun getBluetoothService(): BluetoothService {
        return BluetoothService;
    }

    fun checkBluetoothStatusChange()
    {
        if(BluetoothConnectActivity.Companion.bluetoothService!=null)
        {
            BluetoothService= BluetoothConnectActivity.Companion.bluetoothService!!
            if(BluetoothService.isConnectedToBluetoothDevice)
            {
                bluetoothConnectedFlag=true;
                Toast.makeText(this, "Currently Connected to "+BluetoothService.connectedDeviceName, Toast.LENGTH_LONG).show()
            }
        }
        //val BluetoothStatusButton:Button=findViewById(R.id.button_menuActivityCheckBluetoothStatus);

        if(bluetoothConnectedFlag)
        {
            BluetoothStatusButton.visibility= View.VISIBLE
        }
        else
        {
            BluetoothStatusButton.visibility=View.GONE
        }
    }


}