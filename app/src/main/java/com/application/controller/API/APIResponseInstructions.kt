package com.application.controller.API

import com.google.gson.annotations.SerializedName

data class APIResponseInstructions(
    @SerializedName("distance") val distance: Double,
    @SerializedName("path") var path: List<APIPathData>,
    @SerializedName("commands") val commands: List<String>,
    @SerializedName("obstacle_order") val obstacleOrder: List<Int>,
    @SerializedName("error") val error: String?
)
/**
{'data':
    {'distance': 65.0,
    'path': [
        {'x': 1, 'y': 1, 'd': 0, 's': -1},
        {'x': 4, 'y': 2, 'd': 2, 's': -1},
        {'x': 13, 'y': 2, 'd': 2, 's': -1},
        {'x': 14, 'y': 2, 'd': 2, 's': 5},
        {'x': 13, 'y': 2, 'd': 2, 's': -1},
        {'x': 10, 'y': 1, 'd': 0, 's': -1},
        {'x': 10, 'y': 10, 'd': 0, 's': -1},
        {'x': 10, 'y': 14, 'd': 0, 's': 2},
        {'x': 10, 'y': 13, 'd': 0, 's': -1},
        {'x': 11, 'y': 10, 'd': 6, 's': -1},
        {'x': 10, 'y': 10, 'd': 6, 's': 4}
    ],
    'commands':
        ['FR00', 'FW90', 'FW10', 'SNAP5_C', 'BW10', 'BR00', 'FW90', 'FW40', 'SNAP2_C', 'BW10', 'BR00', 'FW10', 'SNAP4_C', 'FIN'],
    'obstacle_order':
        [5, 2, 4],
    'error': None
}
 *
 */
