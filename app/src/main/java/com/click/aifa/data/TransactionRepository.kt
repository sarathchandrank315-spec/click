package com.click.aifa.data

class TransactionRepository(private val dao: TransactionDao) {

    val allTransaction = dao.getAllTransaction()

    suspend fun insert(transaction: TransactionEntity) {
        dao.insertTransaction(transaction)
    }
}
