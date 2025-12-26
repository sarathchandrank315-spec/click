package com.click.aifa.ui.addTransaction.addIncome

import android.R
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.traceEventEnd
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.click.aifa.data.Category
import com.click.aifa.data.TransactionEntity
import com.click.aifa.data.enums.TransactionType
import com.click.aifa.data.user.UserSession
import com.click.aifa.databinding.ActivityAddincomeBinding
import com.click.aifa.ui.adapter.CategoryAdapter
import com.click.aifa.ui.addTransaction.CategoryPreference
import com.click.aifa.viewmodel.IncomeViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

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
        val selectedCategory = if (isExpense) "income" else "expense"
        val familyList: MutableList<String> = mutableListOf()
        UserSession.currentUser?.familyMembers?.map { it.name }?.let {
            familyList.addAll(it)
        }
        UserSession.currentUser?.user?.name?.let {
            familyList.add(it)
        }


        val adapter = ArrayAdapter(
            this,
            R.layout.simple_dropdown_item_1line,
            familyList
        )

        val savedCategories = CategoryPreference.getCategories(this)

        val categoryList = if (savedCategories.isEmpty()) {
            mutableListOf(
                Category(1, "Salary", true),
                Category(2, "Discount"),
                Category(3, "+")
            )
        } else {
            savedCategories
        }
        val spanCount = 3
        val layoutManager = GridLayoutManager(this, spanCount)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (categoryList[position].name == "+") spanCount else 1
            }
        }

        binding.rvCategory.layoutManager = layoutManager


        binding.rvCategory.adapter = CategoryAdapter(categoryList) {
            showAddCategoryDialog(categoryList)
        }

        binding.editEarner.setAdapter(adapter)
        binding.editEarner.keyListener = null      // disable typing
        binding.editEarner.setOnClickListener {
            binding.editEarner.showDropDown()       // force show dropdown
        }
        binding.btnAddIncome.setOnClickListener {

            val title = binding.editIncomeTitle.text.toString()
            val amount = binding.editIncome.text.toString().toDoubleOrNull() ?: 0.0
            val payeePayer = binding.editEarner.text.toString()
            val currentDate = binding.calendarView.date
            val income = TransactionEntity(
                user = UserSession.currentUser?.user.toString(),
                title = title,
                amount = amount,
                category = categoryList.first { it.isSelected}.toString(),
                date = currentDate,
                type = if (isExpense) TransactionType.EXPENSE else TransactionType.INCOME,
                payeePayer = payeePayer
            )

            viewModel.insertIncome(income)
            Toast.makeText(this, "Income Added!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    class GridSpacingItemDecoration(
        private val spacing: Int
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.set(spacing, spacing, spacing, spacing)
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
                    binding.rvCategory.adapter?.notifyDataSetChanged()

                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}
