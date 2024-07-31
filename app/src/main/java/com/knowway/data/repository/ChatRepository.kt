package com.knowway.data.repository

import com.knowway.data.model.ChatMessage
import com.knowway.data.model.SendMessage
import com.knowway.data.network.ChatApiService
import retrofit2.Response

class ChatRepository(private val api: ChatApiService) {
    suspend fun getMessages(storeId: Long): List<ChatMessage> = api.getMessages(storeId)
    suspend fun postMessage(message: SendMessage): Response<Void> = api.postMessage(message)
}
