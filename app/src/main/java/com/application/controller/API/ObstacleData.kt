package com.application.controller.API

import com.google.gson.annotations.SerializedName

data class ObstacleData(
    @SerializedName("x") val x: Int,
    @SerializedName("y") val y: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("d") val d: Int)

/*
*
* data class ObstacleData(
    @SerializedName("x") val x: Int,
    @SerializedName("y") val y: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("d") val d: Int)
*
* */

//{"x": 12, "y": 15, "id": 1, "d": 4}
