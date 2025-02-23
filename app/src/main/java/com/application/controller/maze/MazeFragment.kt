package com.application.controller.maze

import android.graphics.Color
import android.content.ClipData
import android.icu.lang.UCharacter.getDirection
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.application.controller.CommunicationActivity
import com.application.controller.R
import com.application.controller.spinner.ObstacleSpinnerAdapter
import com.application.controller.spinner.ObstacleSelectorAdapter
import kotlinx.coroutines.*

//libraries for Json Parsing:
import org.json.JSONObject
import org.json.JSONArray

class MazeFragment : Fragment() {

    private lateinit var mazeView: MazeView // Reference to the MazeView
    private lateinit var spinnerRobotX: Spinner
    private lateinit var spinnerRobotY: Spinner
    private lateinit var spinnerRobotDirection: Spinner
    private lateinit var spinnerObstacleType: Spinner
    private lateinit var spinnerSelectObstacleType: Spinner

    // Variables to track the robot's current position and direction
    private var robotX = 1
    private var robotY = 1
    private var robotDirection = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_maze, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateBluetoothStatus(CommunicationActivity.bluetoothService?.isConnectedToBluetoothDevice == true)
        updateBluetoothConnectedDevice(CommunicationActivity.bluetoothService?.connectedDeviceName)



        // Initialize MazeView
        mazeView = view.findViewById(R.id.maze_view)

        // Initialize spinners
        spinnerRobotX = view.findViewById(R.id.spinner_robot_x)
        spinnerRobotY = view.findViewById(R.id.spinner_robot_y)
        spinnerRobotDirection = view.findViewById(R.id.spinner_robot_direction)
        spinnerObstacleType = view.findViewById(R.id.spinner_obstacle_type)
        spinnerSelectObstacleType = view.findViewById(R.id.spinner_select_obstacle_type)



        view.findViewById<Button>(R.id.button_start).setOnClickListener {
            val jsonString = getJsonFromApi() // Function to get JSON from API
            parseJsonAndUpdateMaze(jsonString)
        }

        // Define obstacle images
        val obstacleImages = listOf(
            R.drawable.obs_normal,
            R.drawable.obs_up,
            R.drawable.obs_down,
            R.drawable.obs_left,
            R.drawable.obs_right
        )

        val catPilot = listOf(
            R.drawable.cat_pilot
        )

        val obstacleNames = listOf(
            "Normal Obstacle",
            "Obstacle facing Up",
            "Obstacle facing Down",
            "Obstacle facing Left",
            "Obstacle facing Right"
        )

        // Find the spinner and set the adapter
        val spinnerObstacleType: Spinner = view.findViewById(R.id.spinner_obstacle_type)
        val adapter = ObstacleSpinnerAdapter(requireContext(), catPilot)
        spinnerObstacleType.adapter = adapter
        val obstacleAdapter = ObstacleSpinnerAdapter(requireContext(), catPilot)
        spinnerObstacleType.adapter = obstacleAdapter

        // Set up adapter for selecting an obstacle type (SelectObstacleType)
        val selectAdapter = ObstacleSelectorAdapter(requireContext(), obstacleImages, obstacleNames)
        spinnerSelectObstacleType.adapter = selectAdapter

        // Handle spinner selection changes for SelectObstacleType
        spinnerSelectObstacleType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val obstacleNames = listOf("Normal", "Up", "Down", "Left", "Right")
                val obstacleType = obstacleNames[position]

                Toast.makeText(requireContext(), "$obstacleType is selected", Toast.LENGTH_SHORT).show()
                mazeView.setSelectedObstacleType(obstacleType) // Update the selected type
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Set listener to update obstacle ID
        spinnerObstacleType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedObstacle = position + 1 // Update ID dynamically
                Log.d("MazeFragment", "Selected Obstacle ID: $selectedObstacle")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerObstacleType.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val position = spinnerObstacleType.selectedItemPosition
                val clipData = ClipData.newPlainText("obstacle_type", position.toString())
                val shadow = View.DragShadowBuilder(v)
                v.startDragAndDrop(clipData, shadow, v, 0)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }



        // Handle Undo button
        view.findViewById<Button>(R.id.button_undo).setOnClickListener {
            mazeView.undoLastAction()
            Toast.makeText(context, "Last action undone!", Toast.LENGTH_SHORT).show()
        }

        // Handle robot movement buttons
        view.findViewById<Button>(R.id.button_move_up).setOnClickListener {
            robotY = (robotY + 1).coerceAtMost(MazeView.ROW_NUM - 1) // Ensure within bounds
            mazeView.updateRobotPosition(robotX, robotY, 0) // Update in the MazeView
            robotDirection = 0 // Update direction
        }

        view.findViewById<Button>(R.id.button_move_down).setOnClickListener {
            robotY = (robotY - 1).coerceAtLeast(0) // Ensure within bounds
            mazeView.updateRobotPosition(robotX, robotY, 180) // Update in the MazeView
            robotDirection = 180 // Update direction
        }

        view.findViewById<Button>(R.id.button_move_left).setOnClickListener {
            robotX = (robotX - 1).coerceAtLeast(0) // Ensure within bounds
            mazeView.updateRobotPosition(robotX, robotY, 270) // Update in the MazeView
            robotDirection = 270 // Update direction
        }

        view.findViewById<Button>(R.id.button_move_right).setOnClickListener {
            robotX = (robotX + 1).coerceAtMost(MazeView.COLUMN_NUM - 1) // Ensure within bounds
            mazeView.updateRobotPosition(robotX, robotY, 90) // Update in the MazeView
            robotDirection = 90 // Update direction
        }

        // Handle "Set Robot" button
        view.findViewById<Button>(R.id.btn_set_robot).setOnClickListener {
            setRobotPosition()
        }

        view.findViewById<Button>(R.id.commsLink).setOnClickListener {
            findNavController().navigate(R.id.action_MazeFragment_to_BluetoothFragment)
            Toast.makeText(context, "Navigating to COMMS LINK...", Toast.LENGTH_SHORT).show()
        }





        // Setup Reset button
        val resetButton: Button = view.findViewById(R.id.button_reset)
        resetButton.setOnClickListener {
            mazeView.resetMaze() // Call the resetMaze function in MazeView
            Toast.makeText(context, "Maze has been reset!", Toast.LENGTH_SHORT).show()
        }

        fun updateBluetoothStatus(isConnected: Boolean) {
            val bluetoothStatusTextView = view?.findViewById<TextView>(R.id.bluetoothStatus)

            if (bluetoothStatusTextView != null) {
                if (isConnected) {
                    bluetoothStatusTextView.text = "Connected"
                    bluetoothStatusTextView.setTextColor(Color.GREEN) // Change to green if connected
                } else {
                    bluetoothStatusTextView.text = "Disconnected"
                    bluetoothStatusTextView.setTextColor(Color.RED) // Change to red if disconnected
                }
            }
        }

    }


    private fun setRobotPosition() {
        // Get the values from the spinners
        val x = spinnerRobotX.selectedItem?.toString()?.toIntOrNull()
        val y = spinnerRobotY.selectedItem?.toString()?.toIntOrNull()
        val direction = spinnerRobotDirection.selectedItem?.toString()?.toIntOrNull()

        if (x == null || y == null || direction == null) {
            Toast.makeText(
                context,
                "Please select valid X, Y, and direction values!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (x !in 0 until MazeView.COLUMN_NUM || y !in 0 until MazeView.ROW_NUM) {
            Toast.makeText(
                context,
                "Position out of bounds! Ensure X and Y are within grid limits.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (direction !in listOf(0, 90, 180, 270)) {
            Toast.makeText(
                context,
                "Invalid direction! Must be 0, 90, 180, or 270.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Update the robot's position and direction in the MazeView
        mazeView.setRobotPosition(x, y, direction)

        // Synchronize the variables in MazeFragment
        robotX = x
        robotY = y
        robotDirection = direction
    }

    private fun resetMaze() {
        // Reset the maze in MazeView
        mazeView.resetMaze()

        // Reset the local robot variables
        robotX = 1
        robotY = 1
        robotDirection = 0

        Toast.makeText(context, "Maze reset to initial state!", Toast.LENGTH_SHORT).show()
    }


    private fun parseJsonAndUpdateMaze(jsonString: String) {
        try {
            val jsonObject = JSONObject(jsonString)
            val pathArray = jsonObject.getJSONArray("path")
            val obstaclesArray = jsonObject.getJSONArray("obstacles")

            // Reset maze before placing obstacles
            mazeView.resetMaze()

            // Add obstacles
            for (i in 0 until obstaclesArray.length()) {
                val obstacle = obstaclesArray.getJSONObject(i)
                val x = obstacle.getInt("x")
                val y = obstacle.getInt("y")
                val order = obstacle.getInt("order")
                mazeView.addObstacleWithNumber(x, y, "Normal", order)
            }

            // Extract robot path (filter out diagonal moves)
            val pathList = mutableListOf<Pair<Int, Int>>()
            for (i in 0 until pathArray.length()) {
                val point = pathArray.getJSONObject(i)
                val x = point.getInt("x")
                val y = point.getInt("y")

                // Only allow horizontal or vertical movement (not diagonal)
                if (pathList.isEmpty() || isStraightMove(pathList.last(), Pair(x, y))) {
                    pathList.add(Pair(x, y))
                }
            }

            Log.d("MazeDebug", "Parsed Path (No Diagonals): $pathList")

            // Move the robot
            startRobotPath(pathList)

        } catch (e: Exception) {
            Log.e("MazeDebug", "Error parsing JSON: ${e.message}")
        }
    }
    private fun isStraightMove(from: Pair<Int, Int>, to: Pair<Int, Int>): Boolean {
        return from.first == to.first || from.second == to.second // Ensure movement is only horizontal or vertical
    }


    private fun getDirection(from: Pair<Int, Int>, to: Pair<Int, Int>): Int {
        return when {
            to.second < from.second -> 180   // Moving UP
            to.first > from.first -> 90   // Moving RIGHT
            to.second > from.second -> 0 // Moving DOWN
            to.first < from.first -> 270  // Moving LEFT
            else -> 90 // Default
        }
    }





    private fun startRobotPath(path: List<Pair<Int, Int>>) {
        CoroutineScope(Dispatchers.Main).launch {
            if (path.isEmpty()) {
                Log.e("MazeDebug", "ERROR: Path is empty, robot has nowhere to go.")
                return@launch
            }

            var prevPoint: Pair<Int, Int>? = null

            for (point in path) {
                if (prevPoint != null) {
                    val direction = getDirection(prevPoint, point) // Get the correct direction
                    mazeView.updateRobotPosition(point.first, point.second, direction)
                } else {
                    mazeView.updateRobotPosition(point.first, point.second, 90) // Default start direction
                }

                prevPoint = point
                delay(500) // Adjust speed of movement
            }
        }
    }



    private fun getJsonFromApi(): String {
        // Mocking the API response, replace with actual API call
        return """
    {
  "path": [
    {"x": 1, "y": 1},
    {"x": 1, "y": 2},
    {"x": 1, "y": 3},
    {"x": 1, "y": 4},
    {"x": 2, "y": 4},
    {"x": 3, "y": 4},
    {"x": 4, "y": 4},
    {"x": 4, "y": 5},
    {"x": 4, "y": 6},
    {"x": 5, "y": 6},
    {"x": 6, "y": 6},
    {"x": 6, "y": 7},
    {"x": 6, "y": 8},
    {"x": 7, "y": 8},
    {"x": 8, "y": 8},
    {"x": 8, "y": 9},
    {"x": 8, "y": 10},
    {"x": 9, "y": 10},
    {"x": 10, "y": 10},
    {"x": 11, "y": 10},
    {"x": 12, "y": 10},
    {"x": 12, "y": 11},
    {"x": 12, "y": 12},
    {"x": 12, "y": 13},
    {"x": 13, "y": 13},
    {"x": 14, "y": 13},
    {"x": 15, "y": 13},
    {"x": 15, "y": 14},
    {"x": 15, "y": 15},
    {"x": 16, "y": 15},
    {"x": 16, "y": 16},
    {"x": 16, "y": 17}
  ],
  "obstacles": [
    {"x": 4, "y": 4, "order": 1},
    {"x": 10, "y": 10, "order": 2},
    {"x": 15, "y": 15, "order": 3},
    {"x": 16, "y": 17, "order": 4}
  ]
}
    """
    }

    // All the buttons lol
    //BLUETOOTH CONNECTION:
    fun updateBluetoothStatus(isConnected: Boolean) {
        val bluetoothStatusTextView = view?.findViewById<TextView>(R.id.bluetoothStatus)

        if (bluetoothStatusTextView != null) {
            bluetoothStatusTextView.text = if (isConnected) "Connected" else "Disconnected"
            bluetoothStatusTextView.setTextColor(if (isConnected) Color.GREEN else Color.RED)
        }
    }

    fun updateBluetoothConnectedDevice(deviceName: String?) {
        val bluetoothDeviceTextView = view?.findViewById<TextView>(R.id.bluetoothConnectedDevice)

        bluetoothDeviceTextView?.apply {
            text = if (!deviceName.isNullOrEmpty()) " $deviceName" else "No Device"
            setTextColor(if (!deviceName.isNullOrEmpty()) Color.BLUE else Color.RED)
        }
    }


}
