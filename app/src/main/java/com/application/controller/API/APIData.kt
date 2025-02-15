package com.application.controller.API

import android.provider.ContactsContract.RawContacts.Data
import com.google.gson.annotations.SerializedName

data class APIData(
    @SerializedName("result") val status: String
)
{
    data class Data(
        val message: String,
        val name: String,
        val status: String
    )

}