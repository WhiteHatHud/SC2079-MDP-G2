package com.application.controller.bluetooth

import android.bluetooth.BluetoothAdapter
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast

import com.application.controller.API.APIPathData
import com.application.controller.API.APIResponseInstructions
import com.application.controller.API.ObstacleData
import com.application.controller.MainActivity
import com.google.android.material.color.utilities.Blend
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.Locale
import kotlin.text.startsWith
import kotlin.text.substring

class BluetoothService {
    /**
     * Return Bluetooth connection status
     *
     * @return Boolean value indicating whether the application is connected to another Bluetooth device
     */
    var isConnectedToBluetoothDevice: Boolean = false
        private set

    /**
     * Return connected Bluetooth device name
     *
     * @return Name of the connected Bluetooth device
     */
    var connectedDeviceName: String? = null
        private set


    var latestMessage: String? = null
        private set

    var persistentMessageLog: String?= ""
        private set

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Public Methods              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Connect to a Bluetooth device.
     *
     * @param macAddress MAC address of the Bluetooth device to be connected
     * @return Boolean value indicating whether connection is successful
     */
    // TODO: Test this method with AMD Tool
    fun connectToBluetoothDevice(macAddress: String): Boolean {
        Log.d(
            BLUETOOTH_SERVICE_TAG,
            "Connecting to device with MAC address: $macAddress"
        )

        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress)
        bluetoothCommunicationService = BluetoothCommunicationService(handler)
        bluetoothCommunicationService.connect(bluetoothDevice, false)

        return isConnectedToBluetoothDevice
    }

    /**
     * Send out message to the connected Bluetooth device
     *
     * @param message The message to be sent out
     */
    fun sendOutMessage(message: String) {
        Log.d(
            BLUETOOTH_SERVICE_TAG,
            "Sending message: $message"
        )
        bluetoothCommunicationService.write(message.toByteArray())
        storeMessageLogSent(message)

        //        processMazeUpdateResponseMessage("ROBOT,CALIBRATING,180,5:10;MDF,000000000000000000000000000000011100000000000000000000000000000000000000000000001110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000;IMAGE,4:5:10,6:6:8"); // TODO: Remove
//        processMazeUpdateResponseMessage("ROBOT,IDLE,0,1:1;MDF,000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000;IMAGE;"); // TODO: Remove
//        processMazeUpdateResponseMessage("P1,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA;P2,CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC;"); // TODO: Remove
    }

    fun sendOutData(message:BluetoothSendData)
    {
        Log.d(
            BLUETOOTH_SERVICE_TAG,
            "Sending Data Type: ${message.cat}"
        )
        val gson=Gson()
        val jsonString=gson.toJson(message)

        val byteArray=jsonString.toByteArray()
        bluetoothCommunicationService.write(byteArray)

    }

    fun sendOutDataObstacle(data:List<ObstacleData>)
    {
        Log.d(
            BLUETOOTH_SERVICE_TAG,
            "Sending Data Type: Obstacle"
        )
        val obstaclesWrapper = ObstaclesWrapper(data, "0")

        // Create an ObstaclesContainer object
        val obstaclesContainer = ObstaclesContainer("obstacles", obstaclesWrapper)

        // Create a Gson instance
        val gson = Gson()

        // Convert the ObstaclesContainer object to a JSON string
        val jsonString = gson.toJson(obstaclesContainer)
        val byteArray=jsonString.toByteArray()
        bluetoothCommunicationService.write(byteArray)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////                  Handler                 ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // This method is adopted from The Android Open Source Project by Google
    /**
     * This handler handles messages from the BluetoothCommunicationService
     */
    // TODO: Test this class on AMD Tool
    private val handler: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                Constants.MESSAGE_STATE_CHANGE -> when (message.arg1) {
                    BluetoothCommunicationService.STATE_CONNECTED -> {
                        Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_CONNECTED")
                        updateIsConnected(true)
                    }

                    BluetoothCommunicationService.STATE_CONNECTING -> {
                        Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_CONNECTING")
                        updateIsConnected(false)
                    }

                    BluetoothCommunicationService.STATE_LISTEN -> {
                        Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_LISTEN")
                        updateIsConnected(false)
                    }

                    BluetoothCommunicationService.STATE_NONE -> {
                        Log.d(BLUETOOTH_SERVICE_HANDLER_TAG, "STATE_NONE")
                        updateIsConnected(false)
                    }
                }

                Constants.MESSAGE_READ -> {
                    val readBytes = message.obj as ByteArray
                    val readMessage = String(readBytes, 0, message.arg1)
                    Log.d(
                        BLUETOOTH_SERVICE_HANDLER_TAG,
                        "MESSAGE_READ - $readMessage"
                    )
                    storeLatestMessage(readMessage)
                    storeMessageLogRecieve(readMessage)
                    // Always display the received text in receive data section in CommunicationFragment
                    //TODO MAZE LOGIC IS HERE
                    // Update maze display if it is maze update response message
                    if (!readMessage.equals("ack", ignoreCase = true)) {
                      //  processMazeUpdateResponseMessage(readMessage)
                    }
                    updateIsConnected(true)
                    if(message.data.getString(Constants.DEVICE_NAME)!=null){
                        connectedDeviceName = message.data.getString(Constants.DEVICE_NAME)
                    }
                    Log.d(
                        BLUETOOTH_SERVICE_HANDLER_TAG,
                        "MESSAGE_DEVICE_NAME - $connectedDeviceName"
                    )
                }

                Constants.MESSAGE_DEVICE_NAME -> {
                    updateIsConnected(true)
                    connectedDeviceName = message.data.getString(Constants.DEVICE_NAME)
                    Log.d(
                        BLUETOOTH_SERVICE_HANDLER_TAG,
                        "MESSAGE_DEVICE_NAME - $connectedDeviceName"
                    )
                }

                Constants.MESSAGE_TOAST -> {
                    val toastMessage = message.data.getString(Constants.TOAST)
                    Log.d(
                        BLUETOOTH_SERVICE_HANDLER_TAG,
                        "MESSAGE_TOAST - $toastMessage"
                    )

                    if (toastMessage.equals(DEVICE_CONNECTION_WAS_LOST, ignoreCase = true)) {

                        // this.isConnectedToBluetoothDevice = false
                        connectedDeviceName =
                            "" // set name to empty string since connection was lost
                       // MainActivity.updateBluetoothStatusFloatingActionButtonDisplay()
                    }
                }
            }
        }
    }

    init {
        bluetoothCommunicationService = BluetoothCommunicationService(handler)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////              Helper Methods              ///////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private fun updateIsConnected(isConnectedValue: Boolean) {
        isConnectedToBluetoothDevice = isConnectedValue
        /*
        MainActivity.updateBluetoothStatusFloatingActionButtonDisplay()*/
    }
  //TODO Store latest sent information and return it if requested
    private fun storeLatestMessage(message: String)
    {
        latestMessage=message;
    }

    private fun storeMessageLogRecieve(message: String)
    {
        persistentMessageLog+= "$connectedDeviceName : $message\n"
    }

    private fun storeMessageLogSent(message: String)
    {
        persistentMessageLog+= "Me : $message\n"
    }


    //PATH LOGIC HERE
    fun processNewMovementData(newInstructionData: APIResponseInstructions)
    {
        val sb = StringBuilder()
        //Distance likely not of use to the Bot, more for display
        val newDistance=newInstructionData.distance
        val newDirection:List<APIPathData> = newInstructionData.path
        var newCommands: MutableList<String> = newInstructionData.commands.toMutableList()

        //Clean or transform turn instructions?
        val prefixes = listOf("FR", "FL", "BL", "BR")

        newCommands.forEachIndexed { index, string ->
            if (prefixes.any { string.startsWith(it) }) {
                newCommands[index] = string.substring(0, 2)
            }
        }
        newCommands.toList()


        //TODO Find Out Best way to pass commands to RPi
        //TODO Most likely way is compiling all commands into the same structure
        //Iterates through newDirection list
        for(pathData in newDirection)
        {
            //Not sure what to do with Path? Probably moving them to Maze to display it
            var pathToString=pathData.x.toString()+","+pathData.y.toString()+","+pathData.d.toString()+","+pathData.s.toString()
        }
        for(command in newCommands)
        {
            //Individually sends out commands separately one after the other?
            sendOutMessage(command)
        }
        //Alternatively maybe sending everything might work?
        //sendOutMessage(newCommands.toString())


    }
/** TODO MAZE LOGIC
    private fun processMazeUpdateResponseMessage(mazeUpdateResponseMessage: String) {
        // Message format: ROBOT,IDLE/RUNNING/CALIBRATING/ARRIVED,0/90/180/270,X:Y;MDF,STRING;IMAGE,X:Y:ID:DIRECTION
        val infoArr = mazeUpdateResponseMessage.split(LEVEL_1_SEPARATOR.toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (info in infoArr) {
            try {
                if (info.startsWith(ROBOT_STRING)) {
                    val robotInfoArr =
                        info.split(LEVEL_2_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val robotStatus = robotInfoArr[1]
                    val robotDirection = robotInfoArr[2].toInt()
                    val robotX = robotInfoArr[3].split(LEVEL_3_SEPARATOR.toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[0].toInt()
                    val robotY = robotInfoArr[3].split(LEVEL_3_SEPARATOR.toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()[1].toInt()

                    MazeFragment.getInstance()
                        .updateRobotDisplay(intArrayOf(robotX, robotY), robotDirection)
                    MazeFragment.getInstance().updateRobotStatus(robotStatus)
                } else if (info.startsWith(MDF_STRING)) {
                    val mdfInfoArr =
                        info.split(LEVEL_2_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val mdfString = mdfInfoArr[1]

                    MazeFragment.getInstance().updateObstacles(mdfString)
                } else if (info.startsWith(IMAGE_STRING)) {
                    val imageInfoArr =
                        info.split(LEVEL_2_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val imageInfoList: MutableList<IntArray> = ArrayList()
                    for (i in 1 until imageInfoArr.size) {
                        val imageInfo = imageInfoArr[i]
                        val imageInfoValues = imageInfo.split(LEVEL_3_SEPARATOR.toRegex())
                            .dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        imageInfoList.add(
                            intArrayOf(
                                imageInfoValues[0].toInt(),  // X
                                imageInfoValues[1].toInt(),  // Y
                                imageInfoValues[2].toInt(),  // ID
                                imageInfoValues[3].toInt() // Direction
                            )
                        )
                    }
                    MazeFragment.getInstance().updateImageInfoList(imageInfoList)
                    //MazeFragment.getInstance().getMazeView().addObstaclesFromImageInfo(imageInfoList);
                } else if (info.startsWith(P1_STRING)) {
                    val p1StringArr =
                        info.split(LEVEL_2_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val p1String = p1StringArr[1].uppercase(Locale.getDefault())

                    MazeFragment.getInstance().updateP1String(p1String)
                } else if (info.startsWith(P2_STRING)) {
                    val p2StringArr =
                        info.split(LEVEL_2_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    val p2String = p2StringArr[1].uppercase(Locale.getDefault())

                    MazeFragment.getInstance().updateP2String(p2String)
                }
            } catch (exception: Exception) {
                Log.d(
                    BLUETOOTH_SERVICE_TAG,
                    "Error processing maze update response: " + exception.message
                )
                Log.d(BLUETOOTH_SERVICE_TAG, exception.stackTrace.contentToString())
            }
        }
    }
    **/
    companion object {
        private const val BLUETOOTH_SERVICE_HANDLER_TAG = "BluetoothService Handler"
        const val BLUETOOTH_SERVICE_TAG = "BluetoothService"

        private const val DEVICE_CONNECTION_WAS_LOST = "device connection was lost"

        // Constants for maze update command
        private const val ROBOT_STRING = "ROBOT"
        private const val MDF_STRING = "MDF"
        private const val IMAGE_STRING = "IMAGE"
        private const val P1_STRING = "P1"
        private const val P2_STRING = "P2"
        private const val LEVEL_1_SEPARATOR = ";"
        private const val LEVEL_2_SEPARATOR = ","
        private const val LEVEL_3_SEPARATOR = ":"

        lateinit var bluetoothCommunicationService: BluetoothCommunicationService
        private lateinit var bluetoothAdapter: BluetoothAdapter
    }
}