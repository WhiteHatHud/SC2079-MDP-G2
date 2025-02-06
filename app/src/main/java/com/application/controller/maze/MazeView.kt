package com.example.mdpapplication.ui.maze

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

    init {
        gridLinePaint.color = Color.BLACK
        gridLinePaint.strokeWidth = 2f

        // Paint for grid background
        emptyGridPaint = Paint()
        emptyGridPaint.color = Color.LTGRAY
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        gridSize = min((getWidth() / COLUMN_NUM).toDouble(), (getHeight() / ROW_NUM).toDouble())
            .toInt()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
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

    companion object {
        private const val COLUMN_NUM = 20
        private const val ROW_NUM = 20
    }
}