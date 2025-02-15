package com.application.controller.API

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.controller.R
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

    lateinit var obstacleXCoordEditText:EditText
    lateinit var obstacleYCoordEditText:EditText
    lateinit var obstacleIDEditText:EditText
    lateinit var obstacleDEditText:EditText
    lateinit var buttonAddObstacle:Button
    var ObstacleList= mutableListOf<ObstacleData>()
    lateinit var obstacleRecyclerView: RecyclerView
    lateinit var ObstacleListItemAdapter: ObstacleListItemAdapter

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
                val APIResponseInstructions=APIService.apiService.postPathData(value)
                if (!APIResponseInstructions.commands.isEmpty())
                {
                    processNewMovementData(APIResponseInstructions)
                }
            }catch (e: Exception) {
                // Handle the error
                Log.e("API PATH POST", "Error : ${e.message}")
            }
        }
    }

    fun processNewMovementData(newInstructionData: APIResponseInstructions)
    {
        val sb = StringBuilder()
        val newDistance=newInstructionData.distance
        val newDirection:List<APIPathData> = newInstructionData.robotDir
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