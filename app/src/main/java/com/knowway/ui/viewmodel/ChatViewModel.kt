package com.knowway.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.knowway.data.model.ChatMessage
import com.knowway.data.model.SendMessage
import com.knowway.data.repository.ChatRepository
import com.knowway.data.network.ChatApiService
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> get() = _messages

    private val chatRepository = ChatRepository(ChatApiService.create())

    fun loadMessages(storeId: Long) {
        viewModelScope.launch {
            _messages.value = chatRepository.getMessages(storeId)
        }
    }

    fun sendMessage(message: SendMessage) {
        viewModelScope.launch {
            chatRepository.postMessage(message)
            loadMessages(message.departmentStoreId)
        }
    }

    fun addMessage(chatMessage: ChatMessage) {
        val updatedMessages = _messages.value.orEmpty().toMutableList().apply {
            add(chatMessage)
        }
        _messages.value = updatedMessages
    }
}
