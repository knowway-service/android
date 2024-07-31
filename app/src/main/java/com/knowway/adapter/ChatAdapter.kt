package com.knowway.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.knowway.R
import com.knowway.data.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter(private val currentUserId: Long) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(ChatDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).memberId != currentUserId) R.layout.item_message_sent else R.layout.item_message_received
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return if (viewType == R.layout.item_message_sent) SentMessageViewHolder(view) else ReceivedMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val nicknameTextView: TextView = itemView.findViewById(R.id.textNickname)
        private val timeTextView: TextView = itemView.findViewById(R.id.textTime)

        fun bind(message: ChatMessage) {
            messageTextView.text = message.messageContent
            nicknameTextView.text = message.messageNickname
            timeTextView.text = formatTime(message.createdAt)
        }

        private fun formatTime(timestamp: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return inputFormat.parse(timestamp)?.let { outputFormat.format(it) } ?: ""
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val nicknameTextView: TextView = itemView.findViewById(R.id.textNickname)
        private val timeTextView: TextView = itemView.findViewById(R.id.textTime)

        fun bind(message: ChatMessage) {
            messageTextView.text = message.messageContent
            nicknameTextView.text = message.messageNickname
            timeTextView.text = formatTime(message.createdAt)
        }

        private fun formatTime(timestamp: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return inputFormat.parse(timestamp)?.let { outputFormat.format(it) } ?: ""
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
