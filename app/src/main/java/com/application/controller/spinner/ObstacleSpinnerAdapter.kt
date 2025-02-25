package com.application.controller.spinner

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.view.LayoutInflater
import com.application.controller.R

class ObstacleSpinnerAdapter(private val context: Context, private val obstacleImages: List<Int>) : BaseAdapter() {

    override fun getCount(): Int = obstacleImages.size

    override fun getItem(position: Int): Any = obstacleImages[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        val imageView: ImageView = view.findViewById(R.id.spinner_image)

        imageView.setImageResource(obstacleImages[position]) // Set image
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }
}
