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
                (userText.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    marginStart = 60
                    marginEnd = 0
                }
                imgChat.visibility=View.GONE
                userText.text=msg.text
            } else {
                userText.setBackgroundResource(R.drawable.bg_chat_bot)
                (userText.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    marginStart = 0
                    marginEnd = 60
                }
                imgUser.visibility=View.GONE
                userText.text=msg.text
            }
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
