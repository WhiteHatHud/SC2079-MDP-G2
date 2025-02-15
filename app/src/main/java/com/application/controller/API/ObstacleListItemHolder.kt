package com.application.controller.API

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.controller.R

class ObstacleListItemHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textViewObstacleHead = itemView.findViewById<TextView>(R.id.TextView_ObstacleHeaderLabel)
    val textViewObstacleSub = itemView.findViewById<TextView>(R.id.TextViewObstacleSubtext)
    val buttonRemoveObstacle=itemView.findViewById<Button>(R.id.button_removeObstacle)
}