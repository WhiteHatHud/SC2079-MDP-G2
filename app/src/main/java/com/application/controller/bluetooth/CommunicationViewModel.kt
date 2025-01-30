package com.application.controller.bluetooth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class CommunicationViewModel : ViewModel() {
    private val receivedStringsText = MutableLiveData<String>()

    init {
        receivedStringsText.value = ""
    }

    val text: LiveData<String>
        get() {
            Log.d(
                "CommunicationViewModel",
                "Returning view model received strings: " + receivedStringsText.value
            )
            return receivedStringsText
        }

    fun updateReceivedStringsText(newReceivedString: String) {
        receivedStringsText.value = """
            $newReceivedString
            ${receivedStringsText.value}
            """.trimIndent()
        Log.d(
            "CommunicationViewModel",
            "Updated view model received strings: " + receivedStringsText.value
        )
    }
}