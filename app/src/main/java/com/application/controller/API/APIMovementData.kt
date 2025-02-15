package com.application.controller.API

import com.google.gson.annotations.SerializedName

data class APIMovementData(
    @SerializedName("obstacles") val obstacles: List<ObstacleData>,
    //List of Obstacle objects (x-coord: Int, y-coord: Int, id: Int, d-direction:Int(1-4))
    @SerializedName("retrying") val retrying: Boolean,
    //Boolean value
    @SerializedName("robot_x") val robotX: Int,
    //Int value for X coordinate
    @SerializedName("robot_y") val robotY: Int,
    //Int value for Y coordinate
    @SerializedName("robot_dir") val robotDir: Int,
    //Int value for Direction robot is facing (Degrees)
    @SerializedName("big_turn") val bigTurn: Int
    //Int value for need for big turn (int flag)
)
/*
EXAMPLE:
 "obstacles":
        [{"x": 12, "y": 15, "id": 1, "d": 4}, {"x": 7, "y": 9, "id": 2, "d": 4}, {"x": 17, "y": 4, "id": 3, "d": 4}, {"x": 4, "y": 17, "id": 4, "d": 4},
        {"x": 19, "y": 9, "id": 5, "d": 4}, {"x": 10, "y": 1, "id": 6, "d": 4}, {"x": 2, "y": 5, "id": 7, "d": 4}, {"x": 11, "y": 11, "id": 8, "d": 4}, {"x": 15, "y": 15, "id": 9, "d": 4}, {"x": 13, "y": 13, "id": 10, "d": 4}],
    "retrying": true,
    "robot_x": 1,
    "robot_y": 1,
    "robot_dir": 0,
    "big_turn": 0
    */