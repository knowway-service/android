package com.knowway.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.knowway.R
import com.knowway.adapter.ChatAdapter
import com.knowway.data.model.ChatMessage
import com.knowway.data.model.SendMessage
import com.knowway.ui.viewmodel.ChatViewModel
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private var storeId: Long = 1
    private var memberId: Long = 1
    private lateinit var webSocketClient: WebSocketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        storeId = intent.getLongExtra("storeId", 1)

        recyclerView = findViewById(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(memberId)
        recyclerView.adapter = adapter

        messageInput = findViewById(R.id.chat_input)
        sendButton = findViewById(R.id.send_button)

        viewModel.messages.observe(this) { messages ->
            adapter.submitList(messages)
            recyclerView.scrollToPosition(messages.size - 1) // Scroll to the latest message
        }

        sendButton.setOnClickListener {
            val messageContent = messageInput.text.toString()
            if (messageContent.isNotEmpty()) {
                val sendMessage = SendMessage(memberId, storeId, messageContent, "UserNickname")
                viewModel.sendMessage(sendMessage)
                webSocketClient.send(messageContent)  // WebSocket을 통해 메시지 전송
                messageInput.text.clear()
            }
        }

        viewModel.loadMessages(storeId)

        initializeWebSocket()
    }

    private fun initializeWebSocket() {
        val uri = URI("ws://ip/ws/chat")
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                // 연결이 열렸을 때
            }

            override fun onMessage(message: String?) {
                runOnUiThread {
                    val chatMessage = ChatMessage(
                        0,
                        memberId,
                        message ?: "",
                        "UserNickname",
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                    )
                    viewModel.addMessage(chatMessage)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                // 연결이 닫혔을 때
            }

            override fun onError(ex: Exception?) {
                ex?.printStackTrace()
            }
        }
        webSocketClient.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketClient.close()
    }
}
