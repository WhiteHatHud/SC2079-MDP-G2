package com.application.controller.API

import com.google.gson.annotations.SerializedName

data class RecognisedImage(
    @SerializedName("imageID") val imaqeID: String,
    @SerializedName("obstacleID") val obstacleID: Int)
