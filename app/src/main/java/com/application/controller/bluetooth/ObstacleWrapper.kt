package com.application.controller.bluetooth

import com.application.controller.API.ObstacleData
import com.google.gson.annotations.SerializedName

data class ObstaclesWrapper(
    @SerializedName("obstacles") val obstacles: List<ObstacleData>,
    @SerializedName("mode") val mode: String
)

data class ObstaclesContainer(
    @SerializedName("cat") val cat: String,
    @SerializedName("value") val value: ObstaclesWrapper
)