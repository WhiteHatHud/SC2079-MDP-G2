package com.application.controller.API
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.application.controller.API.APIData
import com.google.firebase.appdistribution.gradle.ApiService
import retrofit2.http.GET
import retrofit2.http.Path

class APIService {
    companion object {
        private const val BASE_URL = "http://10.91.13.44:8000/" // Replace with your API base URL

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        private val gson = GsonBuilder()
            .setLenient()
            .create()

        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiService: APIServiceInterface by lazy {
            retrofit.create(APIServiceInterface::class.java)
        }
    }
}