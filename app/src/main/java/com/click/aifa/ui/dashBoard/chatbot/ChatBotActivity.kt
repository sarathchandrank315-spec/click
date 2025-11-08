package com.click.aifa.ui.dashBoard.chatbot

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.click.aifa.databinding.LayoutChatBinding
import com.click.aifa.ui.dashBoard.chatbot.adapter.ChatAdapter
import com.click.aifa.ui.dashBoard.chatbot.adapter.data.ChatMessage
import com.click.aifa.ui.dashBoard.chatbot.repository.ChatBotRepository
import kotlinx.coroutines.launch

class ChatBotActivity : AppCompatActivity() {

    private lateinit var binding: LayoutChatBinding

    private val chatAdapter = ChatAdapter()
    private val repository = ChatBotRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSendButton()
    }

    private fun setupRecyclerView() {
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = chatAdapter
    }

    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                chatAdapter.addMessage(ChatMessage(message, true))
                binding.messageInput.text.clear()

                lifecycleScope.launch {
                    val response = repository.getFinanceResponse(message)
                    Log.d("TAG", "setupSendButton: $response")
                    chatAdapter.addMessage(ChatMessage(response.replace("*",""), false))
                    binding.chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
                }
            }
        }
    }
}
