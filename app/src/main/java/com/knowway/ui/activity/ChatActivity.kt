package com.knowway.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowway.BuildConfig
import com.knowway.Constants
import com.knowway.R
import com.knowway.adapter.ChatAdapter
import com.knowway.data.model.ChatMessage
import com.knowway.data.model.SendMessage
import com.knowway.ui.viewmodel.ChatViewModel
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import java.text.SimpleDateFormat
import kotlin.random.Random
import java.util.*

class ChatActivity : AppCompatActivity() {
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private var storeId: Long = 2
    private var memberId: Long = 3
    private lateinit var webSocketClient: WebSocketClient
    private lateinit var userNickname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        storeId = intent.getLongExtra("storeId", 2)
        memberId = intent.getLongExtra("memberId", 3)

        userNickname = generateRandomNickname()

        recyclerView = findViewById(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(memberId)
        recyclerView.adapter = adapter

        messageInput = findViewById(R.id.chat_input)
        sendButton = findViewById(R.id.send_button)

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        })

        viewModel.messages.observe(this) { messages ->
            adapter.submitList(messages)
        }

        sendButton.setOnClickListener {
            val messageContent = messageInput.text.toString()
            if (messageContent.isNotEmpty()) {
                val sendMessage = SendMessage(memberId, storeId, messageContent, userNickname)
                viewModel.sendMessage(sendMessage)
                val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).format(Date())
                val jsonMessage = JSONObject().apply {
                    put("type", "chatMessage")
                    put("memberId", memberId)
                    put("messageContent", messageContent)
                    put("userNickname", userNickname)
                    put("timestamp", timestamp)
                }
                webSocketClient.send(jsonMessage.toString())
                messageInput.text.clear()
            }
        }

        viewModel.loadMessages(storeId)

        initializeWebSocket()
    }

    private fun generateRandomNickname(): String {
        val adjective = Constants.adjectives[Random.nextInt(Constants.adjectives.size)]
        val noun = Constants.nouns[Random.nextInt(Constants.nouns.size)]
        return "$adjective $noun"
    }

    private fun initializeWebSocket() {
        val uri = URI("ws://${BuildConfig.BASE_IP_ADDRESS}:8080/ws/chat/$storeId")
        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                // 연결이 열렸을 때
            }

            override fun onMessage(message: String?) {
                runOnUiThread {
                    message?.let {
                        try {
                            val jsonObject = JSONObject(it)
                            if (jsonObject.has("type")) {
                                when (jsonObject.getString("type")) {
                                    "userCount" -> {
                                        val userCount = jsonObject.getInt("count")
                                        findViewById<TextView>(R.id.chatroom_user_count).text = userCount.toString()
                                    }
                                    "chatMessage" -> {
                                        val senderId = jsonObject.getLong("memberId")
                                        val messageContent = jsonObject.getString("messageContent")
                                        val senderNickname = jsonObject.getString("userNickname")
                                        val timestamp = jsonObject.getString("timestamp")

                                        val chatMessage = ChatMessage(
                                            0,
                                            senderId,
                                            messageContent,
                                            senderNickname,
                                            timestamp
                                        )

                                        viewModel.addMessage(chatMessage)
                                    }
                                }
                            } else {
                                Log.w("WebSocket", "Message received with no type: $it")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
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
