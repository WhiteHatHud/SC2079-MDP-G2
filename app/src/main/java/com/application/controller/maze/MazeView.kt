package com.application.controller.maze

import android.content.ClipData
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.application.controller.CommunicationActivity
import com.application.controller.R
import java.util.Stack
import kotlin.math.min

class MazeView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var gridSize = 0
    // Increase the left, right, top, and bottom margin for better dragging
    private val outerMargin = 150 // Increase this value for more space

    //obstacle
    // Stores obstacle directions
    private val obstacleDirectionMap: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()

    // Paint for grid lines
    private val gridLinePaint = Paint()
    private val emptyGridPaint = Paint()
    private val labelPaint = Paint()
    private val zonePaint = Paint()
    private val targetIDPaint = Paint().apply {
        color = Color.RED   // Make it stand out
        textSize = 40f      // Bigger size for visibility
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true // Make it bold for emphasis
    }
    //for the path
    private val pathMap: MutableList<Pair<Int, Int>> = mutableListOf()

    private var obstacleID = 1
    private val obstacleIDMap: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()

    //Obstacle Selector type:
    private var selectedObstacleType: String = "Up"

    // Tank images
    private val robotBitmaps: Map<Int, Bitmap> = mapOf(
        0 to BitmapFactory.decodeResource(resources, R.drawable.tank_up),
        90 to BitmapFactory.decodeResource(resources, R.drawable.tank_right),
        180 to BitmapFactory.decodeResource(resources, R.drawable.tank_down),
        270 to BitmapFactory.decodeResource(resources, R.drawable.tank_left)
    )

    // Obstacle images
    private val obstacleBitmaps: Map<String, Bitmap> = mapOf(
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
    //track drag review
    private var previewX: Int? = null
    private var previewY: Int? = null
    private var isDraggingObstacle = false
    private var previewObstacleType: String? = null



    init {
        gridLinePaint.color = Color.BLACK
        gridLinePaint.strokeWidth = 2f

        emptyGridPaint.color = Color.parseColor("#C2B280")

        labelPaint.color = Color.BLACK
        labelPaint.textSize = 20f
        labelPaint.textAlign = Paint.Align.CENTER

        zonePaint.color = Color.GREEN
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        val yLabelMargin = 50 // Space for Y labels
        gridSize = min((width - leftMargin) / COLUMN_NUM, (height - leftMargin) / ROW_NUM)
        leftMargin = (width - (COLUMN_NUM * gridSize)) / 2 + 10
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        drawLabels(canvas)
        drawRobot(canvas)
        drawObstacles(canvas) // Ensure obstacles are drawn
        drawPath(canvas)
    }

    private fun drawPath(canvas: Canvas) {
        val pathPaint = Paint().apply {
            color = Color.BLUE
            strokeWidth = 5f
        }
        for (i in 1 until pathMap.size) {
            val start = pathMap[i - 1]
            val end = pathMap[i]
            canvas.drawLine(
                (start.first * gridSize + gridSize / 2 + leftMargin).toFloat(),
                ((ROW_NUM - start.second - 1) * gridSize + gridSize / 2).toFloat(),
                (end.first * gridSize + gridSize / 2 + leftMargin).toFloat(),
                ((ROW_NUM - end.second - 1) * gridSize + gridSize / 2).toFloat(),
                pathPaint
            )
        }
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
        val labelOffset = gridSize * 0.4f
        val xLabelMargin = gridSize * 0f
        val yLabelOffset = 10f


        for (i in 0 until COLUMN_NUM) {
            canvas.drawText(
                i.toString(),
                (i * gridSize + leftMargin + labelOffset).toFloat(),
                ((ROW_NUM + 0.5) * gridSize - xLabelMargin).toFloat(), // 🛠 Moves X labels slightly higher
                labelPaint
            )
        }

        for (j in 0 until ROW_NUM) {
            canvas.drawText(
                j.toString(),
                leftMargin * 0.3f, // 🛠 Moves Y labels slightly to the left
                ((ROW_NUM - j - 0.5) * gridSize + yLabelOffset).toFloat(), // 🛠 Adjusts label position
                labelPaint
            )
        }
    }

    private fun drawObstacles(canvas: Canvas) {
        for ((position, type) in obstacleMap) {
            val (x, y) = position
            val left = x * gridSize + leftMargin
            val top = (ROW_NUM - y - 1) * gridSize

            // ✅ Get the correct bitmap based on the type
            val obstacleBitmap = obstacleBitmaps[type]

            if (obstacleBitmap != null) {
                val scaledBitmap = Bitmap.createScaledBitmap(obstacleBitmap, gridSize, gridSize, false)
                canvas.drawBitmap(scaledBitmap, left.toFloat(), top.toFloat(), null)

                // Check if this obstacle is in the targeted list
                val isTargeted = targetedObstacles.contains(position)

                // Update text paint properties
                val textSize = if (isTargeted) 40f else 20f // Make it even larger
                val textColor = if (isTargeted) Color.RED else Color.WHITE

                // Apply to labelPaint
                labelPaint.color = textColor
                labelPaint.textSize = textSize

                // Draw the obstacle number
                obstacleNumbersMap[position]?.let { number ->
                    canvas.drawText(
                        number.toString(),
                        (left + gridSize / 2).toFloat(),
                        (top + gridSize / 1.5).toFloat(),
                        labelPaint
                    )
                }

                // Draw the obstacle ID
                obstacleIDMap[position]?.let { id ->
                    canvas.drawText(
                        id.toString(),
                        (left + gridSize / 2).toFloat(),
                        (top + gridSize / 1.2).toFloat(),
                        labelPaint
                    )
                }
            } else {
                Log.e("MazeView", "❌ Error: No bitmap found for type: $type at ($x, $y)")
            }
        }
    }


    private fun rotateBitmap(source: Bitmap, angle: Int): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }


    private val obstacleNumbersMap: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()
    fun addObstacleWithNumber(x: Int, y: Int, type: String, number: Int) {
        if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM && type in obstacleBitmaps.keys) {
            saveState()
            obstacleMap[Pair(x, y)] = type
            obstacleNumbersMap[Pair(x, y)] = number

            invalidate()
        }
    }

    fun addObstacle(x: Int, y: Int, type: String) {
        if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM) {
            saveState()

            val validTypes = listOf("Up", "Down", "Left", "Right")
            val validatedType = if (type in validTypes) type else "Up" // ✅ Ensure valid type
            obstacleMap[Pair(x, y)] = validatedType
            obstacleIDMap[Pair(x, y)] = obstacleID++

            val obstacleDirection = getObstacleDirection(validatedType)
            obstacleDirectionMap[Pair(x, y)] = obstacleDirection

            // Send Add Obstacle Message via Bluetooth
            val obstacleId = obstacleIDMap[Pair(x, y)] ?: 0
            CommunicationActivity.sendAddObstacleMessage(x, y, obstacleId, obstacleDirection)

            Log.d("MazeView", "Added Obstacle: $validatedType at ($x, $y) with Direction: $obstacleDirection")

            invalidate()
        }
    }



    fun updateRobotPosition(x: Int, y: Int, direction: Int) {
        Log.d("MazeDebug", "Updating robot to ($x, $y) with direction: $direction")

        if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM) {
            saveState()
            robotX = x
            robotY = y
            robotDirection = direction
            pathMap.add(Pair(x, y))
            invalidate() // Redraw the view
        } else {
            Log.e("MazeDebug", "Invalid position! ($x, $y) out of bounds.")
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
        private var leftMargin = 50
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
    obstacleIDMap.clear()
    obstacleID = 1
    pathMap.clear()

    // Send Bluetooth Reset Command
    CommunicationActivity.sendResetCommand()

    // Redraw the maze
    invalidate()

    Log.d("MazeView", "Maze has been reset. Sending reset command via Bluetooth.")
}

    fun undoLastAction() {
        if (stateStack.isNotEmpty()) {
            val previousState = stateStack.pop()
            robotX = previousState.robotX
            robotY = previousState.robotY
            robotDirection = previousState.robotDirection
            obstacleMap.clear()
            obstacleMap.putAll(previousState.obstacleMap)
            obstacleID = obstacleMap.size + 1
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

    override fun onDragEvent(event: DragEvent?): Boolean {
        when (event?.action) {

            DragEvent.ACTION_DRAG_STARTED -> {
                isDraggingObstacle = true
                invalidate() // Redraw to show preview
            }

            DragEvent.ACTION_DRAG_LOCATION -> {

                val x = ((event.x - leftMargin + gridSize / 2) / gridSize).toInt()
                val y = (ROW_NUM - 1 - ((event.y + gridSize / 2) / gridSize).toInt())

                // Update preview position
                previewX = x
                previewY = y
                invalidate() // Redraw the preview
            }

            DragEvent.ACTION_DROP -> {
                isDraggingObstacle = false
                val clipData = event.clipData?.getItemAt(0)?.text?.toString()
                val coordinates = clipData?.split(",")?.map { it.toIntOrNull() }

                previewX = null
                previewY = null

                val x = ((event.x - leftMargin + gridSize / 2) / gridSize).toInt()
                val y = (ROW_NUM - 1 - ((event.y + gridSize / 2) / gridSize).toInt())

                // Ensure obstacle type is correctly assigned
                val obstacleType = selectedObstacleType
                val obstacleDirection = getObstacleDirection(obstacleType) // Ensure direction is set

                if (coordinates != null && coordinates.size == 2) {
                    val originalX = coordinates[0]!!
                    val originalY = coordinates[1]!!

                    if (x < 0 || x >= COLUMN_NUM || y < 0 || y >= ROW_NUM) {
                        removeObstacleFromGrid(originalX, originalY)
                        Log.d("MazeView", "Obstacle removed at: ($originalX, $originalY)")
                    } else {
                        if (obstacleMap.containsKey(Pair(originalX, originalY))) {
                            moveObstacle(originalX, originalY, x, y)
                            Toast.makeText(context, "Obstacle moved to ($x, $y)", Toast.LENGTH_SHORT).show()
                            Log.d("MazeView", "Obstacle moved from ($originalX, $originalY) to ($x, $y)")
                        }
                    }
                } else {
                    if (!obstacleIDMap.containsKey(Pair(x, y))) {
                        addObstacle(x, y, obstacleType) // ✅ Add the correct type
                        val obstacleId = obstacleIDMap[Pair(x, y)] ?: 0
                        CommunicationActivity.sendAddObstacleMessage(x, y, obstacleId, obstacleDirection)

                        Toast.makeText(context, "Dropped $obstacleType at ($x, $y)", Toast.LENGTH_SHORT).show()
                        Log.d("MazeView", "Obstacle added at: ($x, $y) | Type: $obstacleType | Direction: $obstacleDirection°")
                    } else {
                        Toast.makeText(context, "Obstacle already exists at ($x, $y)", Toast.LENGTH_SHORT).show()
                    }
                }

                invalidate()
            }



            DragEvent.ACTION_DRAG_ENDED -> {
                isDraggingObstacle = false
                previewX = null
                previewY = null
                invalidate() // Remove preview
            }
        }
        return true
    }



    // Function to update the selected obstacle type
    fun setSelectedObstacleType(type: String) {
        val validTypes = listOf("Up", "Down", "Left", "Right")
        selectedObstacleType = if (type in validTypes) type else "Up" // ✅ Always ensure a valid type
        Log.d("MazeView", "Selected Obstacle Type updated to: $selectedObstacleType")
    }


    //To remove the obstacles
    fun removeObstacle(x: Int, y: Int) {
        if (obstacleMap.containsKey(Pair(x, y))) {
            obstacleMap.remove(Pair(x, y))
            obstacleIDMap.remove(Pair(x, y))
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = ((event.x - leftMargin) / gridSize).toInt()
                val y = (ROW_NUM - 1 - (event.y / gridSize).toInt())

                // Check if an obstacle exists at this location
                if (obstacleMap.containsKey(Pair(x, y))) {
                    startDraggingObstacle(x, y)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }


    private fun startDraggingObstacle(x: Int, y: Int) {
        val clipData = ClipData.newPlainText("obstacle", "$x,$y")
        val shadowBuilder = View.DragShadowBuilder(this)
        startDragAndDrop(clipData, shadowBuilder, null, 0)

        previewObstacleType = obstacleMap[Pair(x, y)] ?:  "Normal" // Store obstacle type for preview
        isDraggingObstacle = true
        previewX = x
        previewY = y
        invalidate() // Redraw to show preview

        Log.d("MazeView", "Started dragging obstacle at ($x, $y)")
    }


    fun moveObstacle(oldX: Int, oldY: Int, newX: Int, newY: Int) {
        if (obstacleMap.containsKey(Pair(oldX, oldY))) {
            val type = obstacleMap[Pair(oldX, oldY)]!!
            val id = obstacleIDMap[Pair(oldX, oldY)]!!
            val direction = obstacleDirectionMap[Pair(oldX, oldY)] ?: 0 // Maintain direction

            // Remove from old position
            obstacleMap.remove(Pair(oldX, oldY))
            obstacleIDMap.remove(Pair(oldX, oldY))
            obstacleDirectionMap.remove(Pair(oldX, oldY))

            // Place at new position
            obstacleMap[Pair(newX, newY)] = type
            obstacleIDMap[Pair(newX, newY)] = id
            obstacleDirectionMap[Pair(newX, newY)] = direction

            // Send Bluetooth Message for Obstacle Movement
            CommunicationActivity.sendMoveObstacleMessage(oldX, oldY, newX, newY, id)

            invalidate() // Redraw the grid
            Log.d("MazeView", "Obstacle moved from ($oldX, $oldY) to ($newX, $newY)")
        }
    }



    fun removeObstacleFromGrid(x: Int, y: Int) {
        val position = Pair(x, y)

        // ✅ Check if the obstacle exists before removing it
        if (obstacleMap.containsKey(position)) {
            val type = obstacleMap[position]!!
            val id = obstacleIDMap[position] ?: -1

            // ✅ Remove obstacle
            obstacleMap.remove(position)
            obstacleIDMap.remove(position)
            invalidate()

            // ✅ Show confirmation toast
            Toast.makeText(context, "Obstacle $id ($type) at ($x, $y) removed from grid", Toast.LENGTH_SHORT).show()
            Log.d("MazeView", "Obstacle $id ($type) at ($x, $y) removed from grid")
        } else {
            // ✅ If no obstacle exists at this location
            Toast.makeText(context, "No obstacle found at ($x, $y) to remove", Toast.LENGTH_SHORT).show()
            Log.d("MazeView", "Tried to remove obstacle at ($x, $y) but none found")
        }
    }
    private val targetedObstacles: MutableSet<Pair<Int, Int>> = mutableSetOf()

    fun updateObstacleTarget(targets: List<Pair<Int, Int>>) {
        Log.d("MazeView", "Updating multiple obstacles with new IDs...")
        Log.d("MazeView", "Existing Obstacle IDs: ${obstacleIDMap.values}")

        var updated = false
        val updatedPositions = mutableSetOf<Pair<Int, Int>>() // Track updated positions

        for ((targetId, newId) in targets) {
            val targetPositions = obstacleIDMap.filterValues { it == targetId }.keys

            if (targetPositions.isNotEmpty()) {
                for (position in targetPositions) {
                    if (position in updatedPositions) continue // Prevent duplicate updates

                    val (x, y) = position
                    val currentType = obstacleMap[position] ?: "Normal"

                    // Update the obstacle with new ID
                    obstacleMap[position] = currentType
                    obstacleIDMap[position] = newId // Assign the correct new ID

                    // Mark obstacle as targeted for UI changes
                    targetedObstacles.add(position)
                    updatedPositions.add(position)

                    // Log update for debugging
                    Log.d("MazeView", "Obstacle at ($x, $y) -> New ID: $newId (was $targetId)")
                }
                updated = true
            } else {
                Log.e("MazeView", "No obstacle found with ID: $targetId")
            }
        }

        if (!updated) {
            Toast.makeText(context, "No matching obstacles found!", Toast.LENGTH_SHORT).show()
        }

        invalidate() // Redraw the view
    }
    fun getObstacleDirection(type: String): Int {
        return when (type) {
            "Up" -> 0     // ✅ Always default to UP
            "Right" -> 90
            "Down" -> 180
            "Left" -> 270
            else -> 0 // Default UP
        }
    }

    private var selectedObstacleDirection: Int = 0 // Default to Up

    fun setSelectedObstacleDirection(direction: Int) {
        selectedObstacleDirection = direction
        Log.d("MazeView", "Selected Obstacle Direction updated to: $selectedObstacleDirection°")
    }


}
