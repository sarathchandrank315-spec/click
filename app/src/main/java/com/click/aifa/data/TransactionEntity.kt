package com.click.aifa.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.click.aifa.data.enums.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val user:String,
    val title: String,
    val amount: Double,
    val category: String,
    val date: Long,
    val payeePayer: String,
    val type: TransactionType  // INCOME or EXPENSE
)
