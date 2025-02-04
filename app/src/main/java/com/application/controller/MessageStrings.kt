package com.application.controller

interface MessageStrings {
    companion object {
        // Defines target
        const val TO_ARDUINO: String= ""
        const val TO_ALGORITHM: String= ""
        const val TO_RASPBERRY_PI: String=""

        // Robot movement commands
        const val ROBOT_MOVE_FORWARD: String= "{\"cat\": \"control\", \"value\": \"FW10\"}"

        const val ROBOT_TURN_LEFT: String= "{\"cat\": \"control\", \"value\": \"FL\"}"

        const val ROBOT_TURN_RIGHT: String= "{\"cat\": \"control\", \"value\": \"FR\"}"

        const val START_TASK1: String= "{\"cat\": \"control\", \"value\": \"start1\"}"

        const val START_TASK2: String= "{\"cat\": \"control\", \"value\": \"start2\"}"


        // Not in use
        const val WAYPOINT: String= "WAYPOINT"

        const val START_POSITION: String= "START"

        const val MAZE_UPDATE: String= "UPDATE"

        const val INITIATE_CALIBRATION: String= "C"

        const val ENABLE_ALIGNMENT: String= "a"

        const val DISABLE_ALIGNMENT: String= "b"

        const  val ENABLE_EMERGENCY_BRAKE: String= "e"

        const   val DISABLE_EMERGENCY_BRAKE: String= "f"

        const  val RESET: String= "RESET"


    }


}