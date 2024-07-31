package com.knowway.data.network

import com.knowway.BuildConfig
import com.knowway.data.model.ChatMessage
import com.knowway.data.model.SendMessage
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApiService {

    @GET("chats/{departmentStoreId}")
    suspend fun getMessages(@Path("departmentStoreId") departmentStoreId: Long): List<ChatMessage>

    @POST("chats")
    suspend fun postMessage(@Body message: SendMessage): Response<Void>

    companion object {
        fun create(): ChatApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://${BuildConfig.BASE_IP_ADDRESS}:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ChatApiService::class.java)
        }
    }
}
