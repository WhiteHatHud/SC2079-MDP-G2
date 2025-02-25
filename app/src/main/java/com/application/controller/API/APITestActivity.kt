package com.application.controller.API

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.controller.BluetoothConnectActivity
import com.application.controller.MainActivity
import com.application.controller.R
import com.application.controller.bluetooth.BluetoothService
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class APITestActivity : AppCompatActivity(){
    lateinit var DataText:String
    lateinit var checkBoxRetryCheckBox:CheckBox
    lateinit var checkBoxBigTurnCheckBox:CheckBox
    lateinit var editTextXCoord:EditText
    lateinit var editTextYCoord:EditText
    lateinit var editTextBotDir:EditText
    lateinit var BluetoothStatusButton:Button
    lateinit var sendToRpiButton:Button

    lateinit var obstacleXCoordEditText:EditText
    lateinit var obstacleYCoordEditText:EditText
    lateinit var obstacleIDEditText:EditText
    lateinit var obstacleDEditText:EditText
    lateinit var buttonAddObstacle:Button
    var ObstacleList= mutableListOf<ObstacleData>()
    lateinit var obstacleRecyclerView: RecyclerView
    lateinit var ObstacleListItemAdapter: ObstacleListItemAdapter
    var latestAPIResponse: APIResponseInstructions? =null
    private lateinit var BluetoothService : BluetoothService

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_test)
        val testGet: Button = findViewById(R.id.button_TestAPI)
        testGet.setOnClickListener {
            fetchData()
        }
        checkBoxRetryCheckBox=findViewById(R.id.checkBox_Retry)
        checkBoxBigTurnCheckBox=findViewById(R.id.checkBox_BigTurn)
        editTextXCoord=findViewById(R.id.EditView_XCoord)
        editTextYCoord=findViewById(R.id.EditView_YCoord)
        editTextBotDir=findViewById(R.id.EditView_BotDir)
        BluetoothStatusButton=findViewById(R.id.button_apiCheckBluetoothService)
        sendToRpiButton=findViewById(R.id.button_sendInstrunctionsToBot)
        obstacleXCoordEditText=findViewById(R.id.EditTextObstacleXCoord)
        obstacleYCoordEditText=findViewById(R.id.EditTextObstacleYCoord)
        obstacleIDEditText=findViewById(R.id.EditTextObstacleID)
        obstacleDEditText=findViewById(R.id.EditTextObstacleD)
        buttonAddObstacle=findViewById(R.id.button_addObstacleToList)
        buttonAddObstacle.setOnClickListener {
            addObstacle()
            ObstacleListItemAdapter.notifyDataSetChanged()
        }
        obstacleRecyclerView=findViewById(R.id.RecyclerView_ObstacleList)
        ObstacleListItemAdapter=ObstacleListItemAdapter(ObstacleList)
        obstacleRecyclerView.adapter=ObstacleListItemAdapter
        obstacleRecyclerView.layoutManager= LinearLayoutManager(this)

        //  val testPostText: EditText = findViewById(R.id.EditView_APITestData)
        val testPost: Button = findViewById(R.id.button_TestAPIPost)
        testPost.setOnClickListener {
            sendPathData()
        }
        //Checks Bluetooth status and sets Bluetooth Service
        checkBluetoothStatusChange()
        sendToRpiButton.setOnClickListener {
           //DUMMY TEST HERE
            // sendInstructionsToCar()
            sendInstructionsToCar_dummy()
        }
/**
        BluetoothStatusButton.setOnClickListener{ view ->

            Snackbar.make(view, "Currently Connected  to "+BluetoothService.connectedDeviceName, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()

        }*/
    }
    fun addObstacle()
    {
        val obstacleX=obstacleXCoordEditText.text.toString()
        val obstacleY=obstacleYCoordEditText.text.toString()
        val obstacleID=obstacleIDEditText.text.toString()
        val obstacleD=obstacleDEditText.text.toString()
        val newObstacle=ObstacleData(obstacleX.toInt(),obstacleY.toInt(),obstacleID.toInt(),obstacleD.toInt())
        ObstacleList.add(newObstacle)
        obstacleXCoordEditText.text.clear()
        obstacleYCoordEditText.text.clear()
        obstacleIDEditText.text.clear()
        obstacleDEditText.text.clear()
    }

    fun sendPathData()
    {
        val xCoord=editTextXCoord.text.toString().toInt()
        val yCoord=editTextYCoord.text.toString().toInt()
        val botDir=editTextBotDir.text.toString().toInt()
        val retryFlagBool=checkBoxRetryCheckBox.isChecked
        var bigTurnFlag=0
        if(checkBoxBigTurnCheckBox.isChecked)
        {
             bigTurnFlag=1
        }
        else
        {
             bigTurnFlag=0
        }
        val APIPathData=APIMovementData(ObstacleList,retryFlagBool,xCoord,yCoord,botDir,bigTurnFlag)
        postToPath(APIPathData)
    }

    fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val thing = APIService.apiService.getStatus()// Access apiService through the companion object
                var textInfo=thing
                //TEST HERE
                Log.d(
                    "API TEST",
                    "API CALL SUCCESSFUL ${textInfo.toString()}"
                )
                //Toast.makeText(this@APITestActivity, "API CALL SUCCESSFUL", Toast.LENGTH_LONG).show()
               // showDataString()
            } catch (e: Exception) {
                // Handle the error
                Log.d(
                    "API TEST",
                    "API CALL FAILED WITH EXCEPTION MSG :${e.message}"
                )
               // Toast.makeText(this@APITestActivity, "API CALL SUCCESSFUL", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun showDataString()
    {
        val textView: TextView = findViewById(R.id.TextView_APITestData)
        textView.text = DataText
    }
    fun sendData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val createDataRequest = APIData("ok")
                val createDataResponse = APIService.apiService.postData(createDataRequest)
                // Process the createUserResponse data
                Log.d("API TEST POST", "Send Successful: $createDataResponse")
            } catch (e: Exception) {
                // Handle the error
                Log.e("API TEST POST", "Error : ${e.message}")
            }
        }
    }
    fun sendData(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val createDataRequest = APIData("Ok")
                val createDataResponse = APIService.apiService.postData(createDataRequest)
                // Process the createUserResponse data
                Log.d("API TEST POST", "Send Successful: $createDataResponse")
            } catch (e: Exception) {
                // Handle the error
                Log.e("API TEST POST", "Error : ${e.message}")
            }
        }
    }
    fun postToPath(value:APIMovementData)
    {
        CoroutineScope(Dispatchers.IO).launch {
            try{
               // val APIResponseInstructions=APIService.apiService.postPathData(value)
                val response_RAW_JSON:APIResponse = APIService.apiService.postPathData(value)
                val responseString=response_RAW_JSON.toString()
                Log.d("API PATH POST", "RAW : $responseString")
                val apiResponseInstructions: APIResponseInstructions? =response_RAW_JSON.data
                if (apiResponseInstructions!=null)
                {
                    latestAPIResponse=apiResponseInstructions
                   processNewMovementData(apiResponseInstructions)
                }
            }catch (e: Exception) {
                // Handle the error
                Log.e("API PATH POST", "Error : ${e.message}")
            }
        }
        latestAPIResponse?.let { processNewMovementData(it) }
    }

    fun sendInstructionsToCar()
    {
        //com.application.controller.bluetooth.BluetoothService.Companion.processNewMovementData
        //checks bluetoothService is running and latest commands exist
        if(BluetoothService.isConnectedToBluetoothDevice&& latestAPIResponse?.commands?.isNotEmpty() == true)
        {
            try {
                    BluetoothService.processNewMovementData(latestAPIResponse!!)
            }catch (e: Exception) {
                // Handle the error
                Log.e("sendInstructionsToCar_BTService", "Error : ${e.message}")
            }
        }else
        {
            Log.d("sendInstructionsToCar", "No Bluetooth Connection Detected")
        }
    }
    fun sendInstructionsToCar_dummy()
    {
        //SEND DUMMY VERSION HERE FOR TESTING:


        val dummy_distance:Double=65.0
        var dummy_path:List<APIPathData> = listOf(APIPathData(1,1,0,-1),APIPathData(4,2,2,-1),APIPathData(13,2,2,-1),APIPathData(14,2,2,5))
        var dummy_commands:List<String> = listOf("FR00","FW90","FW10","SNAP5_C","BW10","BR00","FW90","FW40","SNAP2_C","BW10","BR00","FW10","SNAP4_C","FIN")
        val dummy_error:String="null"
        val dummyData:APIResponseInstructions=APIResponseInstructions(dummy_distance,dummy_path,dummy_commands,dummy_error)
        if(BluetoothService.isConnectedToBluetoothDevice)
        {
            try {
                BluetoothService.processNewMovementData(dummyData)
            }catch (e: Exception) {
                // Handle the error
                Log.e("sendInstructionsToCar_BTService_DUMMY", "Error : ${e.message}")
            }
        }else
        {
            Log.d("sendInstructionsToCar", "No Bluetooth Connection Detected")
        }
        /*
        *
        * "data": {
        "distance": 65.0,
        "path": [
            {
                "x": 1,
                "y": 1,
                "d": 0,
                "s": -1
            },
            {
                "x": 4,
                "y": 2,
                "d": 2,
                "s": -1
            },
            {
                "x": 13,
                "y": 2,
                "d": 2,
                "s": -1
            },
            {
                "x": 14,
                "y": 2,
                "d": 2,
                "s": 5
            },
            {
                "x": 13,
                "y": 2,
                "d": 2,
                "s": -1
            },
            {
                "x": 10,
                "y": 1,
                "d": 0,
                "s": -1
            },
            {
                "x": 10,
                "y": 10,
                "d": 0,
                "s": -1
            },
            {
                "x": 10,
                "y": 14,
                "d": 0,
                "s": 2
            },
            {
                "x": 10,
                "y": 13,
                "d": 0,
                "s": -1
            },
            {
                "x": 11,
                "y": 10,
                "d": 6,
                "s": -1
            },
            {
                "x": 10,
                "y": 10,
                "d": 6,
                "s": 4
            }
        ],
        "commands": [
            "FR00",
            "FW90",
            "FW10",
            "SNAP5_C",
            "BW10",
            "BR00",
            "FW90",
            "FW40",
            "SNAP2_C",
            "BW10",
            "BR00",
            "FW10",
            "SNAP4_C",
            "FIN"
        ]
    },
    "error": null
}
        *
        * */
    }


    fun checkBluetoothStatusChange()
    {
        var bluetoothConnectedFlag=false
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
            BluetoothStatusButton.isEnabled= true
            sendToRpiButton.isEnabled=true
        }
        else
        {
            BluetoothStatusButton.isEnabled=false
            sendToRpiButton.isEnabled=false
        }
    }

    fun processNewMovementData(newInstructionData: APIResponseInstructions)
    {
        val sb = StringBuilder()
        val newDistance=newInstructionData.distance
        val newDirection:List<APIPathData> = newInstructionData.path
        val newCommands:List<String> = newInstructionData.commands

        var newDirectionToString:String=""
        sb.append(newDirectionToString)
        for(i in newDirection.indices)
        {
            sb
                .append("Direction "+i+1+": x:"+newDirection[i].x.toString()+" y:"+newDirection[i].y.toString()+" d:")
                .append(newDirection[i].d.toString()+" s:"+newDirection[i].s.toString()+"\n")
            //newDirectionToString+="Direction "+i+1+": x:"+newDirection[i].x.toString()+" y:"+newDirection[i].y.toString()+" d:"
        }
        newDirectionToString=sb.toString()
        sb.clear()
        var newCommandsToString:String="List of commands as follows: "
        sb.append(newCommandsToString)
        for(i in newCommands.indices)
        {
            sb.append(newCommands[i]+",")
        }
        newCommandsToString=sb.toString()
        sb.clear()
        var newReceivedInstrunctionMsg:String=
            "Instructions:\nTotal Path Distance "+newDistance.toString()+"\n"+
                    "Robot Directions:\n"+newDirectionToString+"\n"+newCommandsToString
        val textView: TextView = findViewById(R.id.TextView_APITestData)
        textView.text=newReceivedInstrunctionMsg
    }
}