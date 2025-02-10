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
    private val emptyGridPaint= Paint()
    private val labelPaint = Paint()
    private val zonePaint = Paint()
    // paint for the robot
    private val robotPaint = Paint()

    // The Robots position and direction
    private var robotX = 1
    private var robotY = 1
    private var robotDirection = 0 // 0: Up, 90: Right, 180: Down, 270: Left
    //Robot position
    private var robotPosition: Pair<Int, Int> = Pair(1, 1) // Default position of the robot
    private var leftMargin = 50

    init {
        gridLinePaint.color = Color.BLACK
        gridLinePaint.strokeWidth = 2f

        // Paint for grid background
        emptyGridPaint.color = Color.LTGRAY

        //Paint for the robots
        labelPaint.color = Color.BLACK
        labelPaint.textSize = 20f
        labelPaint.textAlign = Paint.Align.CENTER



        robotPaint.color = Color.YELLOW
        zonePaint.color = Color.GREEN
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        val yLabelMargin = 50 // Space for Y labels
        gridSize = min((width - leftMargin) / COLUMN_NUM, (height - leftMargin) / ROW_NUM)
        // Shift the grid to the left by adding margin
        leftMargin = yLabelMargin
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        drawLabels(canvas)
        drawRobot(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        for (i in 0 until COLUMN_NUM) {
            for (j in 0 until ROW_NUM) {
                canvas.drawRect(
                    (i * gridSize + leftMargin).toFloat(), (j * gridSize).toFloat(),
                    ((i + 1) * gridSize + leftMargin).toFloat(), ((j + 1) * gridSize).toFloat(), emptyGridPaint
                )
            }
        }

        // Draw vertical grid lines
        for (i in 0..COLUMN_NUM) {
            canvas.drawLine(
                (i * gridSize + leftMargin).toFloat(), 0f,
                (i * gridSize + leftMargin).toFloat(), (ROW_NUM * gridSize).toFloat(), gridLinePaint
            )
        }

        // Draw horizontal grid lines
        for (j in 0..ROW_NUM) {
            canvas.drawLine(
                leftMargin.toFloat(), (j * gridSize).toFloat(),
                (COLUMN_NUM * gridSize + leftMargin).toFloat(), (j * gridSize).toFloat(), gridLinePaint
            )
        }
    }


    private fun drawRobot(canvas: Canvas) {
        // Draw the green surrounding area
        for (i in robotX - 1..robotX + 1) {
            for (j in robotY - 1..robotY + 1) {
                if (i in 0 until COLUMN_NUM && j in 0 until ROW_NUM) {
                    canvas.drawRect(
                        (i * gridSize).toFloat(), ((ROW_NUM - j - 1) * gridSize).toFloat(),
                        ((i + 1) * gridSize).toFloat(), ((ROW_NUM - j) * gridSize).toFloat(), zonePaint
                    )
                }
            }
        }

        // Draw the robot
        canvas.drawRect(
            (robotX * gridSize).toFloat(), ((ROW_NUM - robotY - 1) * gridSize).toFloat(),
            ((robotX + 1) * gridSize).toFloat(), ((ROW_NUM - robotY) * gridSize).toFloat(), robotPaint
        )
    }

    private fun drawLabels(canvas: Canvas) {
        val labelOffset = gridSize * 0.5f // Center labels inside cells
        val xLabelMargin = gridSize * 0.2f // Moves X labels slightly down

        // X-axis labels (bottom)
        for (i in 0 until COLUMN_NUM) {
            canvas.drawText(
                i.toString(),
                (i * gridSize + leftMargin + labelOffset).toFloat(),
                ((ROW_NUM + 0.3) * gridSize).toFloat(), // Adjusted to prevent clipping
                labelPaint
            )
        }

        // Y-axis labels (left)
        for (j in 0 until ROW_NUM) {
            canvas.drawText(
                j.toString(),
                leftMargin * 0.3f, // Shift Y-labels to the left of the grid
                ((ROW_NUM - j - 0.5) * gridSize).toFloat(),
                labelPaint
            )
        }
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