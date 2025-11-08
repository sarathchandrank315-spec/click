package com.click.aifa.ui.dashBoard.chatbot.repository

import android.icu.util.Currency
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatBotRepository {

    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    suspend fun getFinanceResponse(userMessage: String): String = withContext(Dispatchers.IO) {
        // Filter: Only finance-related questions
        if (!isFinanceRelated(userMessage)) {
            return@withContext "I can only answer questions related to personal finance, savings, investment, or budgeting."
        }

        val prompt = """
            You are a helpful financial assistant. 
            Answer only finance-related questions.
            User: $userMessage
        """.trimIndent()

        try {
            val response = model.generateContent(prompt)
            response.text ?: "Sorry, I couldn’t process that."
        } catch (e: Exception) {
            e.printStackTrace()
            "Something went wrong. Please try again."
        }
    }

    private fun isFinanceRelated(text: String): Boolean {
        val keywords = listOf(
            "money",
            "finance",
            "investment",
            "loan",
            "budget",
            "bank",
            "savings",
            "stock",
            "crypto",
            "income",
            "expense",
            "Currency"
        )
        return keywords.any { text.contains(it, ignoreCase = true) }
    }
}
