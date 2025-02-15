package com.application.controller.API

import com.google.gson.annotations.SerializedName

data class APIResponseInstructions(
    @SerializedName("distance") val distance: Double,
    @SerializedName("path") val robotDir: List<APIPathData>,
    @SerializedName("commands") val commands: List<String>,
    @SerializedName("error") val error: String?
)
/**
{
"data": {
"commands": [
"FR00",
"FW10",
"SNAP1",
"FR00",
"BW50",
"FL00",
"FW60",
"SNAP2",
"FIN"
],
"distance": 46.0,
"path": [
{
"d": 0,
"s": -1,
"x": 1,
"y": 1
},
{
"d": 2,
"s": -1,
"x": 5,
"y": 3
}
]
},
"error": null
}
 *
 */
