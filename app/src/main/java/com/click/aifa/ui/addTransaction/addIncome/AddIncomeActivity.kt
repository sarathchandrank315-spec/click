package com.click.aifa.ui.addTransaction.addIncome


import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.click.aifa.R
import com.click.aifa.data.Category
import com.click.aifa.data.TransactionEntity
import com.click.aifa.data.enums.TransactionType
import com.click.aifa.data.user.UserSession
import com.click.aifa.databinding.ActivityAddincomeBinding
import com.click.aifa.databinding.TopbarLayoutBinding
import com.click.aifa.ui.addTransaction.CategoryPreference
import com.click.aifa.viewmodel.IncomeViewModel

class AddIncomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddincomeBinding
    private lateinit var viewModel: IncomeViewModel
    private var currentDate = Calendar.getInstance().timeInMillis
    private var isExpense = false

    var categoryList =
        mutableListOf(
            Category(1, "Salary", true),
            Category(2, "Discount"),
            Category(3, "Add New")
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddincomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        isExpense = intent.getBooleanExtra("IS_EXPENSE", false)
        val savedCategories = CategoryPreference.getCategories(this)
        if (savedCategories.isNotEmpty()) {
            categoryList = savedCategories
        }
        changeCategory()
        customizeAppBar(binding.topBar)
        val familyList: MutableList<String> = mutableListOf()
        UserSession.currentUser?.familyMembers?.map { it.name }?.let {
            familyList.addAll(it)
        }
        UserSession.currentUser?.user?.name?.let {
            familyList.add(it)
        }


        val familyAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            familyList
        )


        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categoryList
        )
        binding.editCategory.setAdapter(categoryAdapter)
        binding.editCategory.keyListener = null      // disable typing
        binding.editCategory.setOnClickListener {
            binding.editCategory.showDropDown()       // force show dropdown
        }
        binding.editCategory.setOnItemClickListener { parent, view, position, id ->
            if (position == categoryList.lastIndex) {
                showAddCategoryDialog(categoryList)
            }
        }

        binding.editEarner.setAdapter(familyAdapter)
        binding.editEarner.keyListener = null      // disable typing
        binding.editEarner.setOnClickListener {
            binding.editEarner.showDropDown()       // force show dropdown
        }
        binding.calendarView.setOnDateChangeListener { p0, p1, p2, p3 ->
            val calander = Calendar.getInstance()
            calander.set(p1, p2, p3, 0, 0, 0)
            currentDate = calander.timeInMillis
        }
        binding.btnAddIncome.setOnClickListener {

            val title = binding.editIncomeTitle.text.toString()
            val amount = binding.editIncome.text.toString().toDoubleOrNull() ?: 0.0
            val payeePayer = binding.editEarner.text.toString()

            val income = TransactionEntity(
                user = UserSession.currentUser?.user.toString(),
                title = title,
                amount = amount,
                category = categoryList.first { it.isSelected }.toString(),
                date = currentDate,
                type = if (isExpense) TransactionType.EXPENSE else TransactionType.INCOME,
                payeePayer = payeePayer
            )

            viewModel.insertIncome(income)
            Toast.makeText(this, "Income Added!", Toast.LENGTH_SHORT).show()
            finish()
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
        topBar.tvTitle.text = if (isExpense) "Expense" else "Income"
    }

    private fun changeCategory() {
        if (isExpense) {
            binding.btnAddIncome.text = "Add Expense"
            binding.btnAddIncome.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.color_secondary
                )
            )
            binding.txtIncome.text = "Expense Title"
            binding.txtEarned.text = "Spend For"
            binding.calanderBg.setBackgroundColor(
                getColor(
                    R.color.color_secondary_light

                )
            )
        }
    }

    private fun showAddCategoryDialog(categoryList: MutableList<Category>) {
        val editText = EditText(this)
        editText.hint = "Category name"

        AlertDialog.Builder(this)
            .setTitle("Add Category")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val name = editText.text.toString()
                if (name.isNotEmpty()) {
                    categoryList.add(
                        categoryList.size - 1,
                        Category(0, name)
                    )

                    CategoryPreference.saveCategories(this, categoryList)

                    val categoryAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        categoryList
                    )
                    binding.editCategory.setAdapter(categoryAdapter)

                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}
