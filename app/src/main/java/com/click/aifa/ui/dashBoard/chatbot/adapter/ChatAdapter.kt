package com.click.aifa.ui.dashBoard.chatbot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.click.aifa.R
import com.click.aifa.databinding.ItemChatBinding
import com.click.aifa.ui.dashBoard.chatbot.adapter.data.ChatMessage

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    inner class ChatViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val msg = messages[position]

        with(holder.binding) {

            if (msg.isUser) {
                userText.setBackgroundResource(R.drawable.bg_chat_user)
                imgChat.visibility = View.GONE
                imgUser.visibility = View.VISIBLE

                if (msg.isLoading) {
                    userText.text = "..."
                } else {
                    userText.text = msg.text
                }

            } else {
                userText.setBackgroundResource(R.drawable.bg_chat_bot)
                imgUser.visibility = View.GONE
                imgChat.visibility = View.VISIBLE

                if (msg.isLoading) {
                    userText.text = "Typing..."
                } else {
                    userText.text = msg.text
                }
            }
        }
    }
    override fun getItemCount() = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
    private var loadingPosition: Int? = null

    fun showLoading() {
        val loadingMessage = ChatMessage(
            text = "",
            isUser = false,
            isLoading = true
        )
        messages.add(loadingMessage)
        loadingPosition = messages.size - 1
        notifyItemInserted(messages.size - 1)
    }

    fun removeLoading() {
        loadingPosition?.let {
            messages.removeAt(it)
            notifyItemRemoved(it)
            loadingPosition = null
        }
    }
}
