package com.application.controller.maze

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class MazeView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private var gridSize = 0

    // Paint for grid lines
    private val gridLinePaint = Paint()
    private val emptyGridPaint: Paint
    // paint for the robot
    private val robotPaint: Paint

    // The Robots position and direction
    private var robotPosition: Pair<Int, Int> = Pair(1, 1) // Initial robot position (1,1)
    private var robotDirection: Int = 0 // Robot direction (0 = up, 90 = right, 180 = down, 270 = left)

    init {
        gridLinePaint.color = Color.BLACK
        gridLinePaint.strokeWidth = 2f

        // Paint for grid background
        emptyGridPaint = Paint()
        emptyGridPaint.color = Color.LTGRAY

        //Paint for the robots
        robotPaint = Paint()
        robotPaint.color = Color.BLUE
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        gridSize = min((getWidth() / COLUMN_NUM).toDouble(), (getHeight() / ROW_NUM).toDouble())
            .toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        drawRobot(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        for (i in 0 until COLUMN_NUM) {
            for (j in 0 until ROW_NUM) {
                // Draw the grid cells
                canvas.drawRect(
                    (i * gridSize).toFloat(), (j * gridSize).toFloat(),
                    ((i + 1) * gridSize).toFloat(), ((j + 1) * gridSize).toFloat(), emptyGridPaint
                )
            }
        }

        // Draw vertical and horizontal grid lines
        for (i in 0..COLUMN_NUM) {
            canvas.drawLine(
                (i * gridSize).toFloat(),
                0f,
                (i * gridSize).toFloat(),
                (ROW_NUM * gridSize).toFloat(),
                gridLinePaint
            )
        }
        for (j in 0..ROW_NUM) {
            canvas.drawLine(
                0f,
                (j * gridSize).toFloat(),
                (COLUMN_NUM * gridSize).toFloat(),
                (j * gridSize).toFloat(),
                gridLinePaint
            )
        }
    }

    private fun drawRobot(canvas: Canvas) {
        // Get robot's grid coordinates
        val (x, y) = robotPosition

        // Convert grid position to pixel position
        val left = x * gridSize.toFloat()
        val top = y * gridSize.toFloat()
        val right = (x + 1) * gridSize.toFloat()
        val bottom = (y + 1) * gridSize.toFloat()

        // Draw the robot as a filled rectangle
        canvas.drawRect(left, top, right, bottom, robotPaint)
    }

    fun updateRobotPosition(x: Int, y: Int, direction: Int) {
        if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM) {
            robotPosition = Pair(x, y)
            robotDirection = direction
            invalidate() // Redraw the view
        }
    }

    companion object {
        private const val COLUMN_NUM = 20
        private const val ROW_NUM = 20
    }
}