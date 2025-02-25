package com.application.controller.API

import com.google.gson.annotations.SerializedName

data class APIResponse(
    @SerializedName("data") val data: APIResponseInstructions?,
    @SerializedName("error") val error: String?
)
