package com.knowway.data.network

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RecordApiService {
    @Multipart
    @POST("records")
    fun uploadRecord(
        @Part file: MultipartBody.Part,
        @Part("record") record: RequestBody
    ): Call<String>

    companion object {
        private const val BASE_URL = "http://192.168.0.61:8080"

        fun create(): RecordApiService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(RecordApiService::class.java)
        }
    }
}