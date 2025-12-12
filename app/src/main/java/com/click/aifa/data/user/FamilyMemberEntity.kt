package com.click.aifa.data.user;

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        val userId: Int,            // Foreign key to main user
        val name: String,
        val relation: String,
        val age: Int,
        val occupation: String
)

