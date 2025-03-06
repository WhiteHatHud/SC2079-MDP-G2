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
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.application.controller.CommunicationActivity
import com.application.controller.R
import java.util.Stack
import kotlin.math.min
import com.application.controller.API.LatestRouteObject

class MazeView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var gridSize = 0
    private val outerMargin = 150 // Increase this value for more space

    //obstacle variables
    // Stores obstacle directions
    private var obstacleID = 1
    private var selectedObstacleType: String = "Up"
    // To convert into a list data class
    data class ObstacleInfo(
        val x: Int,
        val y: Int,
        val id: Int,
        val direction: Int
    )
    val obstacleInfoList: MutableList<ObstacleInfo> = mutableListOf()
    //Mapping the obstacle ID to new target ID
    private val obstacleImageMap: MutableMap<Int, String> = mutableMapOf() // ✅ Map ObstacleID → ImageID

    fun updateObstacleImageMapping(obstacleID: Int, imageID: String) {
        Log.d("MazeView", "📝 Storing Mapping → Obstacle $obstacleID → Image ID: $imageID")

        obstacleImageMap[obstacleID] = imageID
        Log.d("MazeView", "✅ Updated Mappings: ${obstacleImageMap.toString()}")

        post {
            invalidate()
        }
    }


    fun fetchObstacleInfoList(): List<ObstacleInfo>{
        return obstacleInfoList.toList()
    }

    //To convert type into correct direction d
    private fun getDirectionFromType(type: String): Int {
        return when (type) {
            "Up" -> 0
            "Right" -> 2
            "Down" -> 4
            "Left" -> 6
            else -> 0
        }
    }

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
        val robotBitmap = robotBitmaps[robotDirection] // Get correct bitmap for direction

        if (robotBitmap != null) {
            val scaledBitmap = Bitmap.createScaledBitmap(robotBitmap, gridSize * 3, gridSize * 3, false)
            val left = (robotX - 1) * gridSize + leftMargin
            val top = (ROW_NUM - robotY - 2) * gridSize

            // 🛠 Ensure robot stays within grid bounds
            if (robotX in 0 until COLUMN_NUM && robotY in 0 until ROW_NUM) {
                canvas.drawBitmap(scaledBitmap, left.toFloat(), top.toFloat(), null)
            } else {
                Log.e("MazeView", "❌ Robot Position Out of Bounds -> X: $robotX, Y: $robotY")
            }
        } else {
            Log.e("MazeView", "❌ Robot Bitmap Not Found for Direction: $robotDirection°")
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
        for (obstacle in obstacleInfoList) {
            val (x, y, id, d) = obstacle
            val left = x * gridSize + leftMargin
            val top = (ROW_NUM - y - 1) * gridSize

            // Determine bitmap using direction
            val type = when (d) {
                0 -> "Up"
                2 -> "Right"
                4 -> "Down"
                6 -> "Left"
                else -> "Up"
            }
            val obstacleBitmap = obstacleBitmaps[type]

            if (obstacleBitmap != null) {
                val scaledBitmap = Bitmap.createScaledBitmap(obstacleBitmap, gridSize, gridSize, false)
                canvas.drawBitmap(scaledBitmap, left.toFloat(), top.toFloat(), null)

                // ✅ Correctly fetch `imageID`, if available
                val displayedLabel = if (obstacleImageMap.containsKey(id)) {
                    obstacleImageMap[id] ?: id.toString()
                } else {
                    id.toString() // Default to obstacleID if no mapping exists
                }

                // ✅ Check if this obstacle has been mapped with an `imageID`
                val isTargeted = if (obstacleImageMap.containsKey(id)) {
                    Log.d("MazeView", "🎯 Obstacle $id is targeted with Image ID: ${obstacleImageMap[id]}")
                    true
                } else {
                    Log.d("MazeView", "🚫 Obstacle $id is NOT targeted")
                    false
                }


                // Update text paint properties
                val textSize = if (isTargeted) 40f else 20f
                val textColor = if (isTargeted) Color.RED else Color.WHITE

                // Apply to labelPaint
                labelPaint.color = textColor
                labelPaint.textSize = textSize

                // ✅ Ensure `displayedLabel` is used (imageID if mapped)
                canvas.drawText(
                    displayedLabel, // ✅ Shows imageID if mapped, otherwise obstacleID
                    (left + gridSize / 2).toFloat(),
                    (top + gridSize / 1.5).toFloat(),
                    labelPaint
                )

                Log.d("MazeView", "🖼️ Drawing Obstacle at ($x, $y) with ID: $id → Label: $displayedLabel (Targeted: $isTargeted)")
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



//    fun addObstacleWithNumber(x: Int, y: Int, id: Int, d: Int) {
//        if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM && type in obstacleBitmaps.keys) {
//            saveState()
//
//            obstacleMap[Pair(x, y)] = type
//            obstacleNumbersMap[Pair(x, y)] = number
//
//            invalidate()
//        }
//    }

    fun addObstacle(x: Int, y: Int, type: String) {
        if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM) {
            saveState()

            val id = obstacleInfoList.size + 1
            val direction = getDirectionFromType(type)
            val obstacle = ObstacleInfo(x , y, id, direction)
            obstacleInfoList.add(obstacle)

//            val validTypes = listOf("Up", "Down", "Left", "Right")
//            val validatedType = if (type in validTypes) type else "Up" // ✅ Ensure valid type
//            obstacleMap[Pair(x, y)] = validatedType
//            obstacleIDMap[Pair(x, y)] = obstacleID++
//
//            val obstacleDirection = getObstacleDirection(validatedType)
//            obstacleDirectionMap[Pair(x, y)] = obstacleDirection
//
//            // Send Add Obstacle Message via Bluetooth
//            val obstacleId = obstacleIDMap[Pair(x, y)] ?: 0
//            CommunicationActivity.sendAddObstacleMessage(x, y, obstacleId, obstacleDirection)

            Log.d("MazeView", "Added Obstacle at ($x, $y) with ID: $id and Direction: $direction°")
            invalidate()
        }
    }

    private fun getRobotDataFromBluetooth(): String {
        return CommunicationActivity.getLatestMessage() // Fetch latest message
    }

//    fun updateRobotPosition(x: Int, y: Int, direction: Int) {
//        Log.d("MazeView", "Updating Robot Position -> X: $x, Y: $y, Dir: $direction°")
//
//        // ✅ Ensure the position is valid before updating
//        if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM) {
//            saveState() // Save previous state for undo
//            robotX = x
//            robotY = y
//            robotDirection = direction
//            invalidate() // ✅ Redraw the view
//        } else {
//            Log.e("MazeView", "❌ Invalid Position -> X: $x, Y: $y is out of bounds")
//        }
//    }

    fun updateRobotPosition() {
        val positionData = com.application.controller.API.LatestRouteObject.robotPosition

        if (positionData.size == 3) {
            val x = positionData[0]
            val y = positionData[1]
            val direction = positionData[2]

            Log.d("MazeView", "🟢 updateRobotPosition() CALLED -> X: $x, Y: $y, Dir: $direction°")

            if (x in 0 until COLUMN_NUM && y in 0 until ROW_NUM) {
                robotX = x
                robotY = y
                robotDirection = direction
                invalidate() // ✅ Redraw
            } else {
                Log.e("MazeView", "❌ Position Out of Bounds -> X: $x, Y: $y")
            }
        } else {
            Log.e("MazeView", "❌ Invalid Position Data: $positionData")
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
        val obstacleList: List<ObstacleInfo>
    )
    //reset maZe stuff
    fun resetMaze() {
        // Reset robot to initial position and direction
        saveState()
        robotX = 1
        robotY = 1
        robotDirection = 0

        // Clear all obstacles
        obstacleInfoList.clear()
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
            if (obstacleInfoList.isNotEmpty()){
                obstacleInfoList.removeAt(obstacleInfoList.size - 1)
                obstacleID = obstacleInfoList.size + 1
            } else {
                Toast.makeText(context, "No obstacles to undo!",  Toast.LENGTH_SHORT).show()
            }
            invalidate()
        }
    }

    fun saveState() {
        val currentState = MazeState(
            robotX,
            robotY,
            robotDirection,
            ArrayList(obstacleInfoList) // ✅ Stores the full list of obstacles
        )
        stateStack.push(currentState) // ✅ Push the state into the stack
    }



    // Function to update the selected obstacle type
    fun setSelectedObstacleType(type: String) {
        val validTypes = listOf("Up", "Down", "Left", "Right")
        selectedObstacleType = if (type in validTypes) type else "Up" // ✅ Always ensure a valid type
        Log.d("MazeView", "Selected Obstacle Type updated to: $selectedObstacleType")
    }




    private fun startDraggingObstacle(x: Int, y: Int) {
        val clipData = ClipData.newPlainText("obstacle", "$x,$y")
        val shadowBuilder = View.DragShadowBuilder(this)
        startDragAndDrop(clipData, shadowBuilder, null, 0)
        val obstacle = obstacleInfoList.find { it.x == x && it.y == y }
        val direction = obstacle?.direction ?: 0
        previewObstacleType = getObstacleType(direction)
        previewX = x
        previewY = y
        invalidate() // Redraw to show preview

        Log.d("MazeView", "Started dragging obstacle at ($x, $y)")
    }


    fun moveObstacle(oldX: Int, oldY: Int, newX: Int, newY: Int) {
        val iterator = obstacleInfoList.iterator()
        var movedObstacle: ObstacleInfo? = null

        while (iterator.hasNext()){
            val obstacle = iterator.next()
            if (obstacle.x == oldX && obstacle.y == oldY){
                movedObstacle = obstacle
                iterator.remove()
                break
            }
        }

        if (movedObstacle != null){
            val updatedObstacle = movedObstacle.copy(x = newX, y = newY)
            obstacleInfoList.add(updatedObstacle)
            invalidate()
            Log.d("MazeView", "Obstacle moved from ($oldX, $oldY) to ($newX, $newY)")
        } else {
            Toast.makeText(context, "No obstacle found at ($oldX, $oldY) to move", Toast.LENGTH_SHORT).show()
            Log.d("MazeView", "Tried to move obstacle at ($oldX, $oldY) but none found")
        }
    }
//        if (obstacleMap.containsKey(Pair(oldX, oldY))) {
//            val type = obstacleMap[Pair(oldX, oldY)]!!
//            val id = obstacleIDMap[Pair(oldX, oldY)]!!
//            val direction = obstacleDirectionMap[Pair(oldX, oldY)] ?: 0 // Maintain direction
//
//            // Remove from old position
//            obstacleMap.remove(Pair(oldX, oldY))
//            obstacleIDMap.remove(Pair(oldX, oldY))
//            obstacleDirectionMap.remove(Pair(oldX, oldY))
//
//            // Place at new position
//            obstacleMap[Pair(newX, newY)] = type
//            obstacleIDMap[Pair(newX, newY)] = id
//            obstacleDirectionMap[Pair(newX, newY)] = direction
//
//            // Send Bluetooth Message for Obstacle Movement
//            CommunicationActivity.sendMoveObstacleMessage(oldX, oldY, newX, newY, id)
//
//            invalidate() // Redraw the grid
//            Log.d("MazeView", "Obstacle moved from ($oldX, $oldY) to ($newX, $newY)")
//        }



    fun removeObstacleFromGrid(x: Int, y: Int) {
        // ✅ Check if the obstacle exists before removing it
        val iterator = obstacleInfoList.iterator()
        var removedObstacle: ObstacleInfo? = null

        while (iterator.hasNext()) {
            val obstacle = iterator.next()
            if (obstacle.x == x && obstacle.y == y) {
                removedObstacle = obstacle
                iterator.remove() // ✅ Remove from list
                break
            }
        }

        if (removedObstacle != null) {
            invalidate() // ✅ Redraw the maze

            // ✅ Show confirmation toast
            Toast.makeText(
                context,
                "Obstacle ${removedObstacle.id} at ($x, $y) removed from grid",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("MazeView", "Obstacle ${removedObstacle.id} at ($x, $y) removed from grid")
        } else {
            // ✅ If no obstacle exists at this location
            Toast.makeText(context, "No obstacle found at ($x, $y) to remove", Toast.LENGTH_SHORT).show()
            Log.d("MazeView", "Tried to remove obstacle at ($x, $y) but none found")
        }
    }



    private val targetedObstacles: MutableSet<ObstacleInfo> = mutableSetOf()

    fun updateObstacleTarget(targets: List<ObstacleInfo>) {
        Log.d("MazeView", "Updating multiple obstacles with new IDs...")
        Log.d("MazeView", "Existing Obstacle IDs: ${obstacleInfoList.map {it.id}}")

        var updated = false
        val updatedObstacles = mutableListOf<ObstacleInfo>() // Track updated positions

        for (target in targets) {
            val existingObstacle = obstacleInfoList.find { it.x == target.x && it.y == target.y }

            if (existingObstacle != null) {
                // ✅ Update the obstacle's ID and direction while keeping the same (x, y)
                val updatedObstacle = ObstacleInfo(target.x, target.y, target.id, target.direction)

                // ✅ Add to updated list (instead of modifying original directly)
                updatedObstacles.add(updatedObstacle)

                Log.d("MazeView", "Updated obstacle at (${target.x}, ${target.y}) -> New ID: ${target.id}, New Direction: ${target.direction}")
                updated = true
            } else {
                Log.e("MazeView", "No obstacle found at (${target.x}, ${target.y}) to update.")
            }
        }

        if (updated) {
            // ✅ Replace existing obstacles with updated ones
            obstacleInfoList.removeAll { existing -> targets.any { it.x == existing.x && it.y == existing.y } }
            obstacleInfoList.addAll(updatedObstacles)

            invalidate() // ✅ Redraw the view
        } else {
            Toast.makeText(context, "No matching obstacles found!", Toast.LENGTH_SHORT).show()
        }
    }

    // Helper functions
    fun getObstacleDirection(type: String): Int {
        return when (type) {
            "Up" -> 0     // ✅ Always default to UP
            "Right" -> 2
            "Down" -> 4
            "Left" -> 6
            else -> 0 // Default UP
        }
    }

    fun getObstacleType(direction: Int): String {
        return when (direction) {
            0 -> "Up"
            2 -> "Right"
            4 -> "Down"
            6 -> "Left"
            else -> "Up" // Default
        }
    }

    private var selectedObstacleDirection: Int = 0 // Default to Up

    fun setSelectedObstacleDirection(direction: Int) {
        selectedObstacleDirection = direction
        Log.d("MazeView", "Selected Obstacle Direction updated to: $selectedObstacleDirection°")
    }

    // . Functions on tapping and dragging objects
    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            val x = ((event.x - leftMargin) / gridSize).toInt()
            val y = (ROW_NUM - 1 - (event.y / gridSize).toInt())

            val obstacle = obstacleInfoList.find { it.x == x && it.y == y }
            if (obstacle != null) {
                rotateObstacleDirection(obstacle)
                return true
            }
            return false
        }

        override fun onLongPress(event: MotionEvent) {
            val x = ((event.x - leftMargin) / gridSize).toInt()
            val y = (ROW_NUM - 1 - (event.y / gridSize).toInt())

            val obstacle = obstacleInfoList.find { it.x == x && it.y == y }
            if (obstacle != null) {
                startDraggingObstacle(x, y)
            }
        }
    })

    //On touch event is modified
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Ensure GestureDetector processes events first
        val gestureHandled = gestureDetector.onTouchEvent(event)

        // ✅ Allow normal touch events to continue after gesture detection
        if (gestureHandled) {
            return true
        }

        // ✅ Prevent drag conflicts (but allow taps)
        if (isDraggingObstacle && event.action != MotionEvent.ACTION_UP) {
            return false
        }

        if (event.action == MotionEvent.ACTION_UP) {
            performClick()  // 🔹 Calls performClick() to ensure accessibility compatibility
        }

        return true // ✅ Ensures event is fully processed
    }


    //Rotating the obstacle:
    private fun rotateObstacleDirection(obstacle: ObstacleInfo){
        val newDirection = when (obstacle.direction){
            0 -> 2
            2 -> 4
            4 -> 6
            6 -> 0
            else -> 0
        }
        val index = obstacleInfoList.indexOf(obstacle)
        if (index != -1){
            obstacleInfoList[index] = obstacle.copy(direction = newDirection)
            Toast.makeText(
                context,
                "Obstacle ID ${obstacle.id} direction changed to $newDirection°",
                Toast.LENGTH_SHORT
            ).show()
        }
        invalidate()
    }

    override fun performClick(): Boolean {
        super.performClick()  // Ensure any inherited behavior still works
        return true  // Indicate that the click was handled
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
                val obstacleDirection = getObstacleDirection(selectedObstacleType)

                if (coordinates != null && coordinates.size == 2) {
                    val originalX = coordinates[0]!!
                    val originalY = coordinates[1]!!

                    if (x < 0 || x >= COLUMN_NUM || y < 0 || y >= ROW_NUM) {
                        removeObstacleFromGrid(originalX, originalY)
                        Log.d("MazeView", "Obstacle removed at: ($originalX, $originalY)")
                    } else {

                        //checks if obstacle exists in old location
                        val existingObstacle = obstacleInfoList.find{ it.x == originalX && it.y == originalY}
                        if (existingObstacle != null) {
                            moveObstacle(originalX, originalY, x, y)
                            Toast.makeText(context, "Obstacle moved to ($x, $y)", Toast.LENGTH_SHORT).show()
                            Log.d("MazeView", "Obstacle moved from ($originalX, $originalY) to ($x, $y)")
                        }
                    }
                } else {
                    val existingObstacle = obstacleInfoList.find{ it.x == x && it.y == y}

                    if (existingObstacle == null) {
                        val obstacleId = obstacleInfoList.size + 1 //adds the new Obstacle ID
                        val newObstacle = ObstacleInfo(x,y,obstacleId,obstacleDirection)

                        //add to the list
                        obstacleInfoList.add(newObstacle)

                        //CommunicationActivity.sendAddObstacleMessage(x, y, obstacleId, obstacleDirection)

                        Toast.makeText(context, "Dropped $obstacleDirection at ($x, $y)", Toast.LENGTH_SHORT).show()
                        Log.d("MazeView", "Obstacle added at: ($x, $y) | Type: $obstacleDirection | Direction: $obstacleDirection°")
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

    //code to update target ID
//    fun updateObstacleImage(targets: List<ObstacleInfo>) {
//        Log.d("MazeView", "Updating obstacles...")
//
//        var updated = false
//        val updatedObstacles = mutableListOf<ObstacleInfo>()
//
//        for (target in targets) {
//            val existingObstacle = obstacleInfoList.find { it.id == target.id }
//
//            if (existingObstacle != null) {
//                // ✅ Keep the original obstacle ID but update its mapped image ID
//                updatedObstacles.add(existingObstacle)
//                updated = true
//            } else {
//                Log.e("MazeView", "No obstacle found for ID: ${target.id}")
//            }
//        }
//
//        if (updated) {
//            invalidate() // ✅ Redraw the view
//        } else {
//            Toast.makeText(context, "No matching obstacles found!", Toast.LENGTH_SHORT).show()
//        }
//    }

    fun updateObstacleImage() {
        val foundImages = com.application.controller.API.LatestRouteObject.foundImage

        Log.d("MazeView", "🔄 Updating Obstacle Images from foundImage List: $foundImages")

        for (image in foundImages) {
            val obstacleId = image.obstacleID.toInt()
            val imageId = image.imageID

            // ✅ Update map directly
            obstacleImageMap[obstacleId] = imageId

            Log.d("MazeView", "✅ Mapped Obstacle $obstacleId → Image ID: $imageId")
        }

        invalidate() // ✅ Redraw maze
    }


    // ✅ Function to update the image ID mapping


}
