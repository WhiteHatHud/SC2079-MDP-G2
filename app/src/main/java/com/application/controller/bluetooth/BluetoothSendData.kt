package com.application.controller.bluetooth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class BluetoothSendData(
    val cat:String,
    val value:String
)