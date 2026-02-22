package com.click.aifa.data.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users",indices = [Index(value = ["phone"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val password: String,
    val age: Int,
    val occupation: String
)
