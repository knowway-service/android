package com.knowway.data.network

import com.knowway.BuildConfig
import com.knowway.data.model.chat.ChatPageResponse
import com.knowway.data.model.chat.SendMessage
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ChatApiService {

    @GET("/chats/{departmentStoreId}")
    suspend fun getMessages(
        @Path("departmentStoreId") departmentStoreId: Long,
        @Query("page") page: Int
    ): ChatPageResponse

    @POST("/chats")
    suspend fun postMessage(@Body message: SendMessage): Response<Void>
}
