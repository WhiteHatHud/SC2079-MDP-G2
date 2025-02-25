package com.application.controller.API

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView
import com.application.controller.R

class ObstacleListItemAdapter (private val ObstacleList:MutableList<ObstacleData>):RecyclerView.Adapter<ObstacleListItemHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObstacleListItemHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_obstacles, parent, false)
        return ObstacleListItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: ObstacleListItemHolder, position: Int) {
        val currentItem = ObstacleList[position]
        holder.textViewObstacleHead.text = "Obstacle "+currentItem.id.toString()
        holder.textViewObstacleSub.text = "x:"+currentItem.x.toString()+", y:"+currentItem.y.toString()+", d:"+currentItem.d.toString()
        holder.buttonRemoveObstacle.setOnClickListener {
            ObstacleList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, ObstacleList.size)
        }
    // Load image into holder.itemImageView using a library like Glide or Picasso
    }

    override fun getItemCount() = ObstacleList.size
    }

