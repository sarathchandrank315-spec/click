package com.click.aifa.ui.addTransaction.addIncome

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.click.aifa.data.TransactionEntity
import com.click.aifa.data.enums.TransactionType
import com.click.aifa.databinding.ActivityAddincomeBinding
import com.click.aifa.viewmodel.IncomeViewModel

class AddIncomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddincomeBinding
    private lateinit var viewModel: IncomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddincomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[IncomeViewModel::class.java]

        // Example Selected category:
        val selectedCategory = "Salary"

        binding.btnAddIncome.setOnClickListener {

            val title = binding.editIncomeTitle.text.toString()
            val amount = binding.editIncome.text.toString().toDoubleOrNull() ?: 0.0
            val currentDate = System.currentTimeMillis()

            val income = TransactionEntity(
                title = title,
                amount = amount,
                category = selectedCategory,
                date = currentDate,
                type = TransactionType.INCOME,
                payeePayer ="Sarath"
            )

            viewModel.insertIncome(income)
            Toast.makeText(this, "Income Added!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
