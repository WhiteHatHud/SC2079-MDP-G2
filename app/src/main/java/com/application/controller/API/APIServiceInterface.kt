package com.application.controller.API

import com.application.controller.API.APIData
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface APIServiceInterface {
    @GET("/")
    suspend fun getDefault(@Path("test") data: String): APIData

    @GET("/test")
    suspend fun getData(@Path("test") data: String): APIData

    //ACTUAL API CALL
    @GET("/status")
    suspend fun getStatus(): APIData

    @POST("/testSend")
    suspend fun postData(@Body createDataRequest: APIData): APIData

    //ACTUAL API CALL
    @POST("/path")
    suspend fun postPathData(@Body postData: APIMovementData): APIResponse
}
