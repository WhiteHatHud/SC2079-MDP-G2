package com.application.controller.API

object LatestRouteObject {
    var latestAPIInfo: APIResponseInstructions?=null
    var latestRobotPosition:String=""
    var robotPosition:MutableList<Int> = mutableListOf()
    var positionChangedFlag:Boolean=false
    var targetObstacle:String=""
    var newTargetObstacleFlag:Boolean=false
    var foundImageID:MutableList<String> = mutableListOf()
    var targetMovementOrder:MutableList<Int> = mutableListOf()
    var foundImage:MutableList<RecognisedImage> = mutableListOf()
}