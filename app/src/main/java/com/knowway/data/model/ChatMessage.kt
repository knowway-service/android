package com.knowway.data.model

data class ChatMessage(
    val messageId: Long,
    val memberId: Long,
    val messageContent: String,
    val messageNickname: String,
    val createdAt: String
)

data class SendMessage(
    val memberId: Long,
    val departmentStoreId: Long,
    val messageContent: String,
    val messageNickname: String
)