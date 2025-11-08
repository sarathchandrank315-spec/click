package com.click.aifa.ui.addTransaction

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.click.aifa.R
import com.click.aifa.databinding.ActivityAddBinding
import com.click.aifa.databinding.TopbarLayoutBinding
import com.click.aifa.ui.addTransaction.adapter.TransactionAdapter
import com.click.aifa.ui.addTransaction.addIncome.AddIncomeActivity
import com.click.aifa.util.Test

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customizeAppBar(binding.topBar)
        // RecyclerView setup
        val transactions = Test.getSampleData()

        binding.recyclerTransactions.layoutManager = LinearLayoutManager(this)
        binding.recyclerTransactions.adapter = TransactionAdapter(transactions)
        binding.btnAddIncome.setOnClickListener {
            val intent = Intent(this, AddIncomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun customizeAppBar(topBar: TopbarLayoutBinding) {
        topBar.leftButton.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.ic_back
            )
        )
        topBar.btnMenu.setOnClickListener {
            finish()
        }
        topBar.btnNotification.visibility = View.INVISIBLE
        topBar.tvTitle.text = "Transactions"
    }

}
