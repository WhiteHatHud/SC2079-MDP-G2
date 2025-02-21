package com.application.controller.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.application.controller.R

class ObstacleSelectorAdapter(context: Context, private val obstacleImages: List<Int>, private val obstacleNames: List<String>) :
    ArrayAdapter<String>(context, R.layout.spinner_selector, obstacleNames) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.spinner_selector, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.spinnerImage)
        val textView = view.findViewById<TextView>(R.id.spinnerText)

        imageView.setImageResource(obstacleImages[position])
        textView.text = obstacleNames[position]

        return view
    }
}
