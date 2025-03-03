package com.application.controller.API

object LatestRouteObject {
    var latestAPIInfo: APIResponseInstructions?=null
    var latestRobotPosition:String=""
    var positionChangedFlag:Boolean=false
    var targetObstacle:String=""
    var newTargetObstacleFlag:Boolean=false
    var foundImageID:MutableList<String> = mutableListOf()
}