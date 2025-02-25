package com.application.controller.API

import com.google.gson.annotations.SerializedName

data class APIPathData(
    @SerializedName("x") val x: Int,
    @SerializedName("y") val y: Int,
    @SerializedName("s") val s: Int,
    @SerializedName("d") val d: Int)
