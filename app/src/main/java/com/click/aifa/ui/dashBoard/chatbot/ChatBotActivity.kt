package com.click.aifa.ui.dashBoard.chatbot

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.click.aifa.data.TransactionEntity
import com.click.aifa.data.enums.TransactionType
import com.click.aifa.databinding.LayoutChatBinding
import com.click.aifa.ui.dashBoard.chatbot.adapter.ChatAdapter
import com.click.aifa.ui.dashBoard.chatbot.adapter.data.ChatMessage
import com.click.aifa.ui.dashBoard.chatbot.repository.ChatBotRepository
import com.click.aifa.viewmodel.IncomeViewModel
import kotlinx.coroutines.launch

class ChatBotActivity : AppCompatActivity() {

    private lateinit var transactions: List<TransactionEntity>
    private lateinit var binding: LayoutChatBinding

    private val chatAdapter = ChatAdapter()
    private val repository = ChatBotRepository()
    private lateinit var incomeViewModel: IncomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topBar.tvTitle.text = "AIFA"
        binding.topBar.btnNotification.visibility = (View.GONE)
        binding.topBar.leftButton.setOnClickListener {
            finish()
        }
        setupRecyclerView()
        setupSendButton()
        incomeViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        // 4️⃣ Observe LiveData (Realtime updates)
        incomeViewModel.allIncomeList.observe(this) { list ->
            transactions = list
        }
    }

    private fun setupRecyclerView() {
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = chatAdapter
    }

    private fun setupSendButton() {
        chatAdapter.addMessage(ChatMessage("Hi, How can i help you?", false))
        chatAdapter.addMessage(
            ChatMessage(
                "Type and send 'analyse' to get financial suggestions based on your transaction.",
                false
            )
        )
        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString().trim()
            if (message.equals("analyse", true)) {
                analyseBudget()
            } else {
                if (message.isNotEmpty()) {
                    chatAdapter.addMessage(ChatMessage(message, true))
                    // Show loading
                    chatAdapter.showLoading()
                    binding.sendButton.isEnabled=false
                    binding.sendButton.alpha=.5f
                    binding.messageInput.text.clear()

                    lifecycleScope.launch {
                        val response = repository.getFinanceResponse(message)
                        Log.d("TAG", "setupSendButton: $response")
                        // Remove loading
                        binding.sendButton.isEnabled=true
                        binding.sendButton.alpha=1f
                        chatAdapter.removeLoading()
                        chatAdapter.addMessage(ChatMessage(response.replace("*", ""), false))
                        binding.chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
                    }
                }
            }

        }
    }

    private fun analyseBudget() {
        val incomeList = transactions.filter { it.type == TransactionType.INCOME }
        val expenseList = transactions.filter { it.type == TransactionType.EXPENSE }
        if (incomeList.isEmpty() || expenseList.isEmpty())
            chatAdapter.addMessage(ChatMessage("Sorry no sufficient data found", false))
        val totalIncome = incomeList.sumOf { it.amount }
        val totalExpense = expenseList.sumOf { it.amount }
        val savings = totalIncome - totalExpense
        val savingsRate = if (totalIncome > 0)
            (savings / totalIncome) * 100 else 0.0
        val categoryTotals = expenseList
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
        val topCategories = categoryTotals.take(3)
        val highestExpense = expenseList.maxByOrNull { it.amount }
        val frequentCategory = expenseList
            .groupBy { it.category }
            .maxByOrNull { it.value.size }
            ?.key
        val summary = """
            You are a professional financial advisor.

            Analyze the following financial summary and provide:

            1. Financial health score (out of 10)
            2. Spending pattern analysis
            3. Risk warnings (if any)
            4. Budget improvement suggestions
            5. Category-specific savings advice
            6. 3 actionable steps to increase savings
            7. Ideal target savings rate for this user

            Keep the advice practical and easy to follow.

            Here is the financial summary:

                        Total Income: ₹$totalIncome
                        Total Expense: ₹$totalExpense
                        Net Savings: ₹$savings
                        Savings Rate: ${"%.2f".format(savingsRate)}%

                        Top Categories:
                        ${topCategories.joinToString("\n") { "${it.first} - ₹${it.second}" }}

                        Highest Expense:
                        ${highestExpense?.title} - ₹${highestExpense?.amount}

                        Most Frequent Category:
                        $frequentCategory
                        """.trimIndent()

        if (summary.isNotEmpty()) {
            chatAdapter.addMessage(ChatMessage("Analyse", true))
            // Show loading
            chatAdapter.showLoading()
            binding.sendButton.isEnabled=false
            binding.sendButton.alpha=.5f
            binding.messageInput.text.clear()

            lifecycleScope.launch {
                val response = repository.getFinanceResponse(summary)
                Log.d("TAG", "setupSendButton: $response")
                // Show loading
                chatAdapter.removeLoading()
                binding.sendButton.isEnabled=true
                binding.sendButton.alpha=1f
                chatAdapter.addMessage(ChatMessage(response.replace("*", ""), false))
                binding.chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
            }
        }
    }
}
