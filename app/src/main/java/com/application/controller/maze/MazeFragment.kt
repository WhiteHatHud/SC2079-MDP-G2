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
    private lateinit var spinnerObstacleX: Spinner
    private lateinit var spinnerObstacleY: Spinner
    private lateinit var spinnerObstacleType: Spinner

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

        // Initialize MazeView
        mazeView = view.findViewById(R.id.maze_view)

        // Initialize spinners
        spinnerRobotX = view.findViewById(R.id.spinner_robot_x)
        spinnerRobotY = view.findViewById(R.id.spinner_robot_y)
        spinnerRobotDirection = view.findViewById(R.id.spinner_robot_direction)
        spinnerObstacleX = view.findViewById(R.id.spinner_x)
        spinnerObstacleY = view.findViewById(R.id.spinner_y)
        spinnerObstacleType = view.findViewById(R.id.spinner_obstacle_type)


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

        // Setup "Add Obstacle" button
        setupObstacleInput(view)

        // Setup Reset button
        val resetButton: Button = view.findViewById(R.id.button_reset)
        resetButton.setOnClickListener {
            mazeView.resetMaze() // Call the resetMaze function in MazeView
            Toast.makeText(context, "Maze has been reset!", Toast.LENGTH_SHORT).show()
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

    private fun setupObstacleInput(view: View) {
        // Get references to the add obstacle button
        val btnAddObstacle = view.findViewById<Button>(R.id.btn_add_obstacle)

        btnAddObstacle.setOnClickListener {
            // Get selected values from the spinners
            val x = spinnerObstacleX.selectedItem?.toString()?.toIntOrNull()
            val y = spinnerObstacleY.selectedItem?.toString()?.toIntOrNull()
            val obstacleType = spinnerObstacleType.selectedItem?.toString()

            if (x != null && y != null && obstacleType != null) {
                if (x !in 0 until MazeView.COLUMN_NUM || y !in 0 until MazeView.ROW_NUM) {
                    Toast.makeText(
                        context,
                        "Position out of bounds! Ensure X and Y are within grid limits.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // Add obstacle to the MazeView
                mazeView.addObstacle(x, y, obstacleType)
                Toast.makeText(context, "Obstacle added at ($x, $y)!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    context,
                    "Please select valid X, Y, and obstacle type!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    }
}
