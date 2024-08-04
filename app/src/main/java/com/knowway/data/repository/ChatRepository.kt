package com.knowway.data.repository

import com.knowway.data.model.chat.ChatPageResponse
import com.knowway.data.model.chat.SendMessage
import com.knowway.data.network.ChatApiService
import retrofit2.Response

class ChatRepository(private val api: ChatApiService) {
    suspend fun getMessages(storeId: Long, page: Int): ChatPageResponse =
        api.getMessages(storeId, page)
    suspend fun postMessage(message: SendMessage): Response<Void> = api.postMessage(message)
}
