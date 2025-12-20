package com.click.aifa.data

import androidx.lifecycle.LiveData

class TransactionRepository(private val dao: TransactionDao) {

    val allTransaction = dao.getAllTransaction()
    val totalIncome: LiveData<Double> = dao.getTotalIncome()
    val totalExpense: LiveData<Double> = dao.getTotalExpense()

    suspend fun insert(transaction: TransactionEntity) {
        dao.insertTransaction(transaction)
    }
}
