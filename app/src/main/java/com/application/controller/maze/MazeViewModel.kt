package com.application.controller.maze

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MazeViewModel : ViewModel() {
    private val mText = MutableLiveData<String>()

    init {
        mText.value = "This is maze fragment"
    }

    val text: LiveData<String>
        get() = mText
}