package com.click.aifa.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(income: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransaction(): LiveData<List<TransactionEntity>>

    @Delete
    suspend fun deleteIncome(income: TransactionEntity)
}
