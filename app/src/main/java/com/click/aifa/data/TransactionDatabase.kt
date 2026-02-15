package com.click.aifa.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.click.aifa.data.dataUtils.TransactionTypeConverter
import com.click.aifa.utils.Constants


@TypeConverters(TransactionTypeConverter::class)
@Database(entities = [TransactionEntity::class], version = 1, exportSchema = true)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun incomeDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        fun getDatabase(context: Context): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    Constants.DATABASE
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
