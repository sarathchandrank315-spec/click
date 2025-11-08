package com.click.aifa.data.dataUtils

import androidx.room.TypeConverter
import com.click.aifa.data.enums.TransactionType

class TransactionTypeConverter {

    @TypeConverter
    fun fromType(type: TransactionType): String = type.name

    @TypeConverter
    fun toType(value: String): TransactionType = TransactionType.valueOf(value)
}
