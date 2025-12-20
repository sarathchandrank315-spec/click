package com.click.aifa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.click.aifa.data.TransactionDatabase
import com.click.aifa.data.TransactionEntity
import com.click.aifa.data.TransactionRepository
import kotlinx.coroutines.launch

class IncomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    val allIncomeList: LiveData<List<TransactionEntity>>
    val totalIncome: LiveData<Double>
    val totalExpense: LiveData<Double>

    init {
        val dao = TransactionDatabase.getDatabase(application).incomeDao()
        repository = TransactionRepository(dao)
        allIncomeList = repository.allTransaction
        totalIncome = repository.totalIncome
        totalExpense = repository.totalExpense
    }

    fun insertIncome(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.insert(transaction)
        }
    }
}
