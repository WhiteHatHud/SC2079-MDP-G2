package com.application.controller.maze

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.application.controller.R
import java.util.Stack
import kotlin.math.min

class MazeView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var gridSize = 0

    // Paint for grid lines
    private val gridLinePaint = Paint()
    private val emptyGridPaint = Paint()
    private val labelPaint = Paint()
    private val zonePaint = Paint()

    // Tank images
    private val robotBitmaps: Map<Int, Bitmap> = mapOf(
        0 to BitmapFactory.decodeResource(resources, R.drawable.tank_up),
        90 to BitmapFactory.decodeResource(resources, R.drawable.tank_right),
        180 to BitmapFactory.decodeResource(resources, R.drawable.tank_down),
        270 to BitmapFactory.decodeResource(resources, R.drawable.tank_left)
    )

    // Obstacle images
    private val obstacleBitmaps: Map<String, Bitmap> = mapOf(
        "Normal" to BitmapFactory.decodeResource(resources, R.drawable.obs_normal),
        "Up" to BitmapFactory.decodeResource(resources, R.drawable.obs_up),
        "Down" to BitmapFactory.decodeResource(resources, R.drawable.obs_down),
        "Left" to BitmapFactory.decodeResource(resources, R.drawable.obs_left),
        "Right" to BitmapFactory.decodeResource(resources, R.drawable.obs_right)
    )

    // Robot's position and direction
    private var robotX = 1
    private var robotY = 1
    private var robotDirection = 0 // 0: Up, 90: Right, 180: Down, 270: Left

    // Obstacles map
    private val obstacleMap: MutableMap<Pair<Int, Int>, String> = mutableMapOf()
    //stack for states
    private val stateStack: Stack<MazeState> = Stack()

    init {
        gridLinePaint.color = Color.BLACK
        gridLinePaint.strokeWidth = 2f

        emptyGridPaint.color = Color.LTGRAY

        labelPaint.color = Color.BLACK
        labelPaint.textSize = 20f
        labelPaint.textAlign = Paint.Align.CENTER

        zonePaint.color = Color.GREEN
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        val yLabelMargin = 50 // Space for Y labels
        gridSize = min((width - leftMargin) / COLUMN_NUM, (height - leftMargin) / ROW_NUM)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        drawLabels(canvas)
        drawRobot(canvas)
        drawObstacles(canvas) // Ensure obstacles are drawn
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
        val robotBitmap = robotBitmaps[robotDirection]
        robotBitmap?.let {
            val scaledBitmap = Bitmap.createScaledBitmap(it, gridSize * 3, gridSize * 3, false)
            val left = (robotX - 1) * gridSize + leftMargin
            val top = (ROW_NUM - robotY - 2) * gridSize
            canvas.drawBitmap(scaledBitmap, left.toFloat(), top.toFloat(), null)
        }
    }

    private fun drawLabels(canvas: Canvas) {
        val labelOffset = gridSize * 0.5f
        val xLabelMargin = gridSize * 0.2f

        for (i in 0 until COLUMN_NUM) {
            canvas.drawText(
                i.toString(),
                (i * gridSize + leftMargin + labelOffset).toFloat(),
                ((ROW_NUM + 0.3) * gridSize).toFloat(),
                labelPaint
            )
        }

        for (j in 0 until ROW_NUM) {
            canvas.drawText(
                j.toString(),
                leftMargin * 0.3f,
                ((ROW_NUM - j - 0.5) * gridSize).toFloat(),
                labelPaint
            )
        }
    }

    private fun drawObstacles(canvas: Canvas) {
        for ((position, type) in obstacleMap) {
            val (x, y) = position
            val obstacleBitmap = obstacleBitmaps[type]
            obstacleBitmap?.let {
                val scaledBitmap = Bitmap.createScaledBitmap(it, gridSize, gridSize, false)
                val left = x * gridSize + leftMargin
                val top = (ROW_NUM - y - 1) * gridSize
                canvas.drawBitmap(scaledBitmap, left.toFloat(), top.toFloat(), null)
            }
        }
    }

    fun addObstacle(x: Int, y: Int, type: String) {
        if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM && type in obstacleBitmaps.keys) {
            saveState()
            obstacleMap[Pair(x, y)] = type
            invalidate()
        }
    }

    fun updateRobotPosition(x: Int, y: Int, direction: Int) {
        if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM) {
            saveState()
            robotX = x
            robotY = y
            robotDirection = direction
            invalidate() // Redraw the view
        }
    }

    fun setRobotPosition(x: Int, y: Int, direction: Int) {
        if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM && direction in robotBitmaps.keys) {
            saveState()
            robotX = x
            robotY = y
            robotDirection = direction
            invalidate() // Redraw the maze to update the robot's position
        }
    }
    companion object {
        const val COLUMN_NUM = 20
        const val ROW_NUM = 20
        private const val leftMargin = 50
    }
    data class MazeState(
        val robotX: Int,
        val robotY: Int,
        val robotDirection: Int,
        val obstacleMap: Map<Pair<Int, Int>, String>
    )
//reset maZe stuff
    fun resetMaze() {
        // Reset robot to initial position and direction
        saveState()
        robotX = 1
        robotY = 1
        robotDirection = 0

        // Clear all obstacles
        obstacleMap.clear()

        // Redraw the maze
        invalidate()
    }

    fun undoLastAction() {
        if (stateStack.isNotEmpty()) {
            val previousState = stateStack.pop()
            robotX = previousState.robotX
            robotY = previousState.robotY
            robotDirection = previousState.robotDirection
            obstacleMap.clear()
            obstacleMap.putAll(previousState.obstacleMap)
            invalidate()
        }
    }

    fun saveState() {
        val currentState = MazeState(
            robotX,
            robotY,
            robotDirection,
            HashMap(obstacleMap)
        )
        stateStack.push(currentState)
    }
}



//    private fun drawRobot(canvas: Canvas) {
//        // Draw the green surrounding area
//        for (i in robotX - 1..robotX + 1) {
//            for (j in robotY - 1..robotY + 1) {
//                if (i in 0 until COLUMN_NUM && j in 0 until ROW_NUM) {
//                    canvas.drawRect(
//                        (i * gridSize + leftMargin).toFloat(), ((ROW_NUM - j - 1) * gridSize).toFloat(),
//                        ((i + 1) * gridSize + leftMargin).toFloat(), ((ROW_NUM - j) * gridSize).toFloat(), zonePaint
//                    )
//                }
//            }
//        }
//
//        // Draw the robot
//        canvas.drawRect(
//            (robotX * gridSize + leftMargin).toFloat(), ((ROW_NUM - robotY - 1) * gridSize).toFloat(),
//            ((robotX + 1) * gridSize + leftMargin).toFloat(), ((ROW_NUM - robotY) * gridSize).toFloat(), robotPaint
//        )
//    }