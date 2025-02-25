package com.application.controller.bluetooth

/**
 * Defines several constants used between [BluetoothCommunicationService] and the UI.
 */
interface Constants {
    companion object {
        // Message types sent from the BluetoothCommunicationService Handler
        const val MESSAGE_STATE_CHANGE: Int = 1
        const val MESSAGE_READ: Int = 2
        const val MESSAGE_WRITE: Int = 3
        const val MESSAGE_DEVICE_NAME: Int = 4
        const val MESSAGE_TOAST: Int = 5

        // Key names received from the BluetoothCommunicationService Handler
        const val DEVICE_NAME: String = "device_name"
        const val TOAST: String = "toast"
    }
}