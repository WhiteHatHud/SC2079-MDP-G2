package com.application.controller

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.application.controller.bluetooth.BluetoothService
import com.application.controller.maze.MazeFragment
import com.google.android.material.snackbar.Snackbar


class CommunicationActivity : AppCompatActivity() {
    private var mAppBarConfiguration: AppBarConfiguration? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CommunicationActivity.Companion.bluetoothService = BluetoothConnectActivity.bluetoothService

        CommunicationActivity.Companion.receivedTextStrings = ""

        setContentView(com.application.controller.R.layout.activity_communication)

       // val toolbar: Toolbar = findViewById(R.id.toolbar)
      //  setSupportActionBar(toolbar)

        val BackToBluetoothButton = findViewById<Button>(com.application.controller.R.id.button_fromCommunicationToBluetooth)
        BackToBluetoothButton.setOnClickListener {
            finish()
        }

        CommunicationActivity.Companion.bluetoothStatusFloatingActionButton =
            findViewById<Button>(com.application.controller.R.id.bluetoothStatusButton)
        CommunicationActivity.Companion.bluetoothStatusFloatingActionButton.setOnClickListener(View.OnClickListener { view ->
            if (CommunicationActivity.Companion.bluetoothService!!.isConnectedToBluetoothDevice) {
                Snackbar.make(
                    view,
                    CommunicationActivity.Companion.DEVICE_IS_CONNECTED_TO + CommunicationActivity.Companion.bluetoothService!!.connectedDeviceName,
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Action", null).show()
            } else {
                Snackbar.make(
                    view,
                    CommunicationActivity.Companion.NO_BLUETOOTH_DEVICE_CONNECTED,
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Action", null).show()
            }
        })
        CommunicationActivity.Companion.updateBluetoothStatusFloatingActionButtonDisplay()
        /**
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = Builder(
            R.id.nav_maze, R.id.nav_bluetooth, R.id.nav_communication
        )
            .setDrawerLayout(drawer)
            .build()


           val navController = findNavController(this, R.id.nav_host_fragment)



        setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        setupWithNavController(navigationView, navController)
         **/
    }


/**
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.nav_host_fragment)
        return (navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }
*/

    companion object {
        private const val MAIN_ACTIVITY_TAG = "CommunicationActivity"

        private const val NO_BLUETOOTH_DEVICE_CONNECTED =
            "There is currently no Bluetooth device connected"
        private const val DEVICE_IS_CONNECTED_TO = "Your device is connected to "

        private lateinit var bluetoothStatusFloatingActionButton: Button

        var bluetoothService: BluetoothService? = null
         //   get() = CommunicationActivity.Companion.bluetoothService

        var receivedTextStrings: String? = null
          //  get() = CommunicationActivity.Companion.receivedTextStrings
/*
    fun getBluetoothService(): BluetoothService {
        return bluetoothService!!
    }*/
        fun updateBluetoothStatusFloatingActionButtonDisplay() {
              val isConnected = CommunicationActivity.Companion.bluetoothService?.isConnectedToBluetoothDevice == true
              val deviceName = CommunicationActivity.bluetoothService?.connectedDeviceName

              // Update FloatingActionButton color
              CommunicationActivity.Companion.bluetoothStatusFloatingActionButton?.setBackgroundTintList(
                  ColorStateList.valueOf(if (isConnected) Color.CYAN else Color.LTGRAY)
              )

              CommunicationActivity.bluetoothStatusFloatingActionButton?.setBackgroundTintList(
                  ColorStateList.valueOf(if (isConnected) Color.CYAN else Color.LTGRAY)
              )

              // Find the current MazeFragment and update the Bluetooth status TextView
              val activity = CommunicationActivity.Companion.bluetoothStatusFloatingActionButton?.context as? AppCompatActivity
              val fragment = activity?.supportFragmentManager?.findFragmentByTag("MazeFragment") as? MazeFragment

              fragment?.updateBluetoothStatus(isConnected)
              fragment?.updateBluetoothConnectedDevice(deviceName)

          }

        fun updateReceivedTextStrings(newReceivedString: String) {
            CommunicationActivity.Companion.receivedTextStrings = """
                 $newReceivedString
                 ${CommunicationActivity.Companion.receivedTextStrings}
                 """.trimIndent()
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Updated received string text view: " + CommunicationActivity.Companion.receivedTextStrings
            )
        }

        fun resetReceivedTextStrings() {
            CommunicationActivity.Companion.receivedTextStrings = ""
        }

    fun getReceivedTextStrings1(): String {
        return receivedTextStrings!!
    }

    fun getLatestMessage(): String
    {
         if(bluetoothService?.latestMessage !=null) {
             return bluetoothService?.latestMessage!!
         }
        else
        {
            return "No message received"
        }
    }

    fun getMessageLog():String{
        if(bluetoothService?.persistentMessageLog !=null) {
            return bluetoothService?.persistentMessageLog!!
        }
        else
        {
            return "No messages to display"
        }
    }


        ////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////             Send Out Messages            ///////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////
        fun sendRobotMoveForwardCommand() {
            val command: String = MessageStrings.TO_ARDUINO + MessageStrings.ROBOT_MOVE_FORWARD
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending robot move forward command: $command"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(command)
        }

        fun sendRobotTurnLeftCommand() {
            val command: String = MessageStrings.TO_ARDUINO + MessageStrings.ROBOT_TURN_LEFT
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending robot turn left command: $command"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(command)
        }

        fun sendRobotTurnRightCommand() {
            val command: String = MessageStrings.TO_ARDUINO + MessageStrings.ROBOT_TURN_RIGHT
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending robot turn right command: $command"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(command)
        }

        fun sendWaypointPosition(waypointCoordinates: IntArray) {
            val waypointMessage: String =
                (MessageStrings.TO_ALGORITHM + MessageStrings.WAYPOINT).toString() + "," + waypointCoordinates[0] + ":" + waypointCoordinates[1]
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending waypoint message: $waypointMessage"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(waypointMessage)
        }

        fun sendRobotStartPosition(startCoordinates: IntArray) {
            val startPositionMessage: String =
                (MessageStrings.TO_ALGORITHM + MessageStrings.START_POSITION).toString() + "," + startCoordinates[0] + ":" + startCoordinates[1]
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending robot start position message: $startPositionMessage"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(startPositionMessage)
        }

        fun sendMazeUpdateRequest() {
            val mazeUpdateRequestMessage: String =
                MessageStrings.TO_ALGORITHM + MessageStrings.MAZE_UPDATE
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending maze update request message: $mazeUpdateRequestMessage"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(mazeUpdateRequestMessage)
        }

        fun sendStartFastestPathCommand() {
            val startFastestPathCommand: String =
                MessageStrings.TO_ALGORITHM + MessageStrings.START_TASK1
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending start fastest path command: $startFastestPathCommand"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(startFastestPathCommand)
        }

        fun sendStartExplorationCommand() {
            val startExplorationCommand: String =
                MessageStrings.TO_ALGORITHM + MessageStrings.START_TASK2
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending start exploration command: $startExplorationCommand"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(startExplorationCommand)
        }

        fun sendInitiateCalibrationCommand() {
            val initiateCalibrationCommand: String =
                MessageStrings.TO_ALGORITHM + MessageStrings.INITIATE_CALIBRATION
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending initiate calibration command: $initiateCalibrationCommand"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(initiateCalibrationCommand)
        }

        fun sendResetCommand() {
            val resetCommand: String = MessageStrings.TO_ALGORITHM + MessageStrings.RESET
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending reset command: $resetCommand"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(resetCommand)
        }

        fun sendEnableAlignmentCheckAfterMoveCommand() {
            val enableAlignmentCommand: String =
                MessageStrings.TO_ALGORITHM + MessageStrings.ENABLE_ALIGNMENT
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending enable alignment check after move command: $enableAlignmentCommand"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(enableAlignmentCommand)
        }

        fun sendDisableAlignmentCheckAfterMoveCommand() {
            val disableAlignmentCommand: String =
                MessageStrings.TO_ALGORITHM + MessageStrings.DISABLE_ALIGNMENT
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending disable alignment check after move command: $disableAlignmentCommand"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(disableAlignmentCommand)
        }

        fun sendEnableEmergencyBrakeCommand() {
            val enableEmergencyBrakeCommand: String =
                MessageStrings.TO_ALGORITHM + MessageStrings.ENABLE_EMERGENCY_BRAKE
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending enable emergency brake command: $enableEmergencyBrakeCommand"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(enableEmergencyBrakeCommand)
        }

        fun sendDisableEmergencyBrakeCommand() {
            val disableEmergencyBrakeCommand: String =
                MessageStrings.TO_ALGORITHM + MessageStrings.DISABLE_EMERGENCY_BRAKE
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending disable emergency brake command: $disableEmergencyBrakeCommand"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(disableEmergencyBrakeCommand)
        }

        fun sendCommunicationMessage(message: String) {
            val communicationMessage: String = MessageStrings.TO_RASPBERRY_PI + message
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending communication message: $communicationMessage"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage(communicationMessage)
        }

        fun sendMoveObstacleMessage(oldx: Int, oldy: Int, newx: Int, newy: Int, obsID: Int) {
            val communicationMessage: String =
                (MessageStrings.TO_RASPBERRY_PI + "Obstacle " + obsID + " moved from " + oldx + ":" + oldy
                        + " to " + newx + ":" + newy)
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending obstacle message: $communicationMessage\n"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage((communicationMessage))
        }

        fun sendAddObstacleMessage(x: Int, y: Int, obsID: Int, obsDirection: Int) {
            val communicationMessage: String = MessageStrings.TO_RASPBERRY_PI +
                    "{\"cat\": \"obstacles\", \"value\": " +
                    "{\"obstacles\":[{\"x\":" + x + ", \"y\":" + y + ", \"id\":" + obsID + ", \"d\":" + obsDirection + "}], \"mode\": \"0\"}}"
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending add obstacle message: $communicationMessage\n"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage((communicationMessage))
        }

        fun sendRemoveObstacleMessage(x: Int, y: Int, obsID: Int) {
            val communicationMessage: String =
                MessageStrings.TO_RASPBERRY_PI + "Obstacle " + obsID + " at " + x + ":" + y + " removed"
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending remove obstacle message: $communicationMessage\n"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage((communicationMessage))
        }

        fun sendAllObstaclesMessage(obstacleJson: StringBuilder) {
            val communicationMessage: String =
                MessageStrings.TO_RASPBERRY_PI + obstacleJson.toString()
            Log.d(
                CommunicationActivity.Companion.MAIN_ACTIVITY_TAG,
                "Sending all obstacles message: $communicationMessage\n"
            )
            CommunicationActivity.Companion.bluetoothService?.sendOutMessage((communicationMessage))
        }
    }
}