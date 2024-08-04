package com.knowway.data.model.chat

data class ChatMessage(
    val messageId: Long,
    val chatMessageId: Long,
    val messageContent: String,
    val messageNickname: String,
    val createdAt: String
)

data class SendMessage(
    val chatMessageId: Long,
    val departmentStoreId: Long,
    val messageContent: String,
    val messageNickname: String
)