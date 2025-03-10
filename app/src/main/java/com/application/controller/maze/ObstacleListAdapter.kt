package com.application.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.controller.maze.MazeView

class ObstacleListAdapter(private val obstacleList: List<MazeView.ObstacleInfo>) :
    RecyclerView.Adapter<ObstacleListAdapter.ObstacleViewHolder>() {

    class ObstacleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val obstacleText: TextView = itemView.findViewById(R.id.text_obstacle_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObstacleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_obstacle, parent, false)
        return ObstacleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObstacleViewHolder, position: Int) {
        val obstacle = obstacleList[position]
        holder.obstacleText.text = "M: ${obstacle.id} X: ${obstacle.x} Y: ${obstacle.y} D: ${obstacle.direction}"
    }

    override fun getItemCount(): Int {
        return obstacleList.size
    }
}
