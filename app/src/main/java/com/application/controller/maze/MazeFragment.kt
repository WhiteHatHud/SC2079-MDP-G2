package com.application.controller.maze

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.application.controller.R

class MazeFragment : Fragment() {

    private lateinit var mazeView: MazeView // Reference to the MazeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_maze, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize MazeView
        val mazeView = view.findViewById<MazeView>(R.id.maze_view)

        // Initialize robot's position and direction
        var robotX = 1 // Starting X position
        var robotY = 1 // Starting Y position
        var robotDirection = 0 // Initial direction (e.g., 0 for up, 90 for right, etc.)

        // Handle button clicks
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
    }
}
