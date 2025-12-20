package com.click.aifa.ui.addTransaction.addIncome

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.click.aifa.data.TransactionEntity
import com.click.aifa.data.enums.TransactionType
import com.click.aifa.data.user.UserEntity
import com.click.aifa.data.user.UserSession
import com.click.aifa.data.user.UserWithFamily
import com.click.aifa.databinding.ActivityAddincomeBinding
import com.click.aifa.viewmodel.IncomeViewModel

class AddIncomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddincomeBinding
    private lateinit var viewModel: IncomeViewModel
    private var isExpense = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddincomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        isExpense = intent.getBooleanExtra("IS_EXPENSE", false)
        // Example Selected category:
        val selectedCategory = if(isExpense)"income" else "expense"
        val familyList: MutableList<String> = mutableListOf()
        UserSession.currentUser?.familyMembers?.map { it.name }?.let {
            familyList.addAll(it)
        }
        UserSession.currentUser?.user?.name?.let {
            familyList.add(it)
        }


        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            familyList
        )

        binding.editEarner.setAdapter(adapter)
        binding.editEarner.keyListener = null      // disable typing
        binding.editEarner.setOnClickListener {
            binding.editEarner.showDropDown()       // force show dropdown
        }
        binding.btnAddIncome.setOnClickListener {

            val title = binding.editIncomeTitle.text.toString()
            val amount = binding.editIncome.text.toString().toDoubleOrNull() ?: 0.0
            val payeePayer = binding.editEarner.text.toString()
            val currentDate = System.currentTimeMillis()
            val income = TransactionEntity(
                user = UserSession.currentUser?.user.toString(),
                title = title,
                amount = amount,
                category = selectedCategory,
                date = currentDate,
                type = if (isExpense) TransactionType.EXPENSE else TransactionType.INCOME,
                payeePayer = payeePayer
            )

            viewModel.insertIncome(income)
            Toast.makeText(this, "Income Added!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
