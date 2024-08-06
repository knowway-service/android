package com.knowway.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.knowway.data.model.chat.ChatMessage
import com.knowway.data.model.chat.SendMessage
import com.knowway.data.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> get() = _messages

    private val chatRepository = ChatRepository()

    private var currentPage = 0
    private var isLastPage = false

    fun loadInitialMessages(storeId: Long) {
        currentPage = 0
        isLastPage = false
        loadMessages(storeId, true)
    }

    fun loadPreviousMessages(storeId: Long) {
        if (isLastPage) return
        loadMessages(storeId, false)
    }

    private fun loadMessages(storeId: Long, isInitialLoad: Boolean) {
        viewModelScope.launch {
            val response = chatRepository.getMessages(storeId, currentPage)
            val newMessages = response.content
            if (newMessages.size < 20) {
                isLastPage = true
            }
            currentPage++

            _messages.value = if (isInitialLoad) {
                newMessages
            } else {
                // 이전 메시지를 기존 메시지 리스트의 앞에 추가
                newMessages + (_messages.value ?: emptyList())
            }
        }
    }

    fun sendMessage(message: SendMessage) {
        viewModelScope.launch {
            try {
                val response = chatRepository.postMessage(message)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Exception while sending message", e)
            }
        }
    }

    fun addMessage(chatMessage: ChatMessage) {
        val updatedMessages = _messages.value.orEmpty().toMutableList().apply {
            add(chatMessage)
        }
        _messages.value = updatedMessages
    }
}
