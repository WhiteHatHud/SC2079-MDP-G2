package com.application.controller.bluetooth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class BluetoothViewModel : ViewModel() {
    private val mText = MutableLiveData<String>()

    init {
        mText.value = "This is bluetooth fragment"
    }

    val text: LiveData<String>
        get() = mText
}