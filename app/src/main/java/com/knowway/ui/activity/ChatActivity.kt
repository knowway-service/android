package com.knowway.ui.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowway.BuildConfig
import com.knowway.Constants
import com.knowway.R
import com.knowway.adapter.ChatAdapter
import com.knowway.data.model.chat.ChatMessage
import com.knowway.data.model.chat.SendMessage
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
    private var storeId: Long = 1
    private var memberChatId: Long = 44
    private lateinit var webSocketClient: WebSocketClient
    private lateinit var userNickname: String
    private var isLoadingMessages = false
    private lateinit var leaveButton: ImageView
    private lateinit var deptNameTextView: TextView
    private lateinit var deptBranchTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val sharedPreferences = getSharedPreferences("DeptPref", Context.MODE_PRIVATE)
        storeId = sharedPreferences.getLong("dept_id", 1)
        val deptName = sharedPreferences.getString("dept_name", "현대백화점")
        val deptBranch = sharedPreferences.getString("dept_branch", "더현대 서울")

        val appPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        memberChatId = appPreferences.getLong("memberChatId", 44)

        userNickname = generateRandomNickname()

        recyclerView = findViewById(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ChatAdapter(memberChatId)
        recyclerView.adapter = adapter

        messageInput = findViewById(R.id.chat_input)
        sendButton = findViewById(R.id.send_button)

        deptNameTextView = findViewById(R.id.dept_name)
        deptBranchTextView = findViewById(R.id.dept_branch)
        deptNameTextView.text = deptName
        deptBranchTextView.text = deptBranch

        leaveButton = findViewById(R.id.ic_leave)
        leaveButton.setOnClickListener {
            finish()
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (!recyclerView.canScrollVertically(-1)) {
                    recyclerView.scrollToPosition(0)
                }
            }
        })

        viewModel.messages.observe(this) { messages ->
            val previousItemCount = adapter.itemCount
            val previousScrollPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val previousTopView = recyclerView.getChildAt(0)
            val previousTopOffset = previousTopView?.top ?: 0

            adapter.submitList(messages) {
                if (previousItemCount == 0) {
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                } else {
                    (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(previousScrollPosition + messages.size - previousItemCount, previousTopOffset)
                }
            }
            isLoadingMessages = false
        }

        sendButton.setOnClickListener {
            val messageContent = messageInput.text.toString()
            if (messageContent.isNotEmpty()) {
                val sendMessage = SendMessage(memberChatId, storeId, messageContent, userNickname)
                viewModel.sendMessage(sendMessage)
                val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).format(Date())
                val jsonMessage = JSONObject().apply {
                    put("type", "chatMessage")
                    put("chatMessageId", memberChatId)
                    put("messageContent", messageContent)
                    put("userNickname", userNickname)
                    put("timestamp", timestamp)
                }
                webSocketClient.send(jsonMessage.toString())
                messageInput.text.clear()
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0 && !recyclerView.canScrollVertically(-1) && !isLoadingMessages) {
                    isLoadingMessages = true
                    viewModel.loadPreviousMessages(storeId)
                }
            }
        })

        viewModel.loadInitialMessages(storeId)
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
                                        val senderId = jsonObject.getLong("chatMessageId")
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
