package com.application.controller.maze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.application.controller.R

class MazeFragment : Fragment() {

    private lateinit var mazeView: MazeView // Reference to the MazeView
    private lateinit var spinnerRobotX: Spinner
    private lateinit var spinnerRobotY: Spinner
    private lateinit var spinnerRobotDirection: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_maze, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize MazeView
        mazeView = view.findViewById(R.id.maze_view)

        // Initialize robot's position and direction
        var robotX = 1 // Starting X position
        var robotY = 1 // Starting Y position
        var robotDirection = 0 // Initial direction (e.g., 0 for up, 90 for right, etc.)

        // Handle button clicks for robot movement
        view.findViewById<Button>(R.id.button_move_up).setOnClickListener {
            robotY = (robotY - 1).coerceAtLeast(0) // Ensure the robot stays within bounds
            mazeView.updateRobotPosition(robotX, robotY, 0) // Update to "Up"
        }

        view.findViewById<Button>(R.id.button_move_down).setOnClickListener {
            robotY = (robotY + 1).coerceAtMost(19) // Ensure the robot stays within bounds
            mazeView.updateRobotPosition(robotX, robotY, 180) // Update to "Down"
        }

        view.findViewById<Button>(R.id.button_move_left).setOnClickListener {
            robotX = (robotX - 1).coerceAtLeast(0) // Ensure the robot stays within bounds
            mazeView.updateRobotPosition(robotX, robotY, 270) // Update to "Left"
        }

        view.findViewById<Button>(R.id.button_move_right).setOnClickListener {
            robotX = (robotX + 1).coerceAtMost(19) // Ensure the robot stays within bounds
            mazeView.updateRobotPosition(robotX, robotY, 90) // Update to "Right"
        }

        view.findViewById<Button>(R.id.btn_set_robot).setOnClickListener {
            val x = spinnerRobotX.selectedItem.toString().toInt()
            val y = spinnerRobotY.selectedItem.toString().toInt()
            val direction = spinnerRobotDirection.selectedItem.toString().toInt()
            mazeView.setRobotPosition(x, y, direction)
        }

        // Handle obstacle input
        setupObstacleInput(view)
    }

    private fun setupObstacleInput(view: View) {
        // Get references to the input fields and the add button
        val spinnerX = view.findViewById<Spinner>(R.id.spinner_x)
        val spinnerY = view.findViewById<Spinner>(R.id.spinner_y)
        val spinnerObstacleType = view.findViewById<Spinner>(R.id.spinner_obstacle_type)
        val btnAddObstacle = view.findViewById<Button>(R.id.btn_add_obstacle)

        btnAddObstacle.setOnClickListener {
            // Get selected values from the spinners
            val x = spinnerX.selectedItem.toString().toIntOrNull()
            val y = spinnerY.selectedItem.toString().toIntOrNull()
            val obstacleType = spinnerObstacleType.selectedItem.toString()

            if (x != null && y != null) {
                mazeView.addObstacle(x, y, obstacleType) // Add the obstacle to the MazeView
            } else {
                Toast.makeText(context, "Please select valid X and Y values!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
