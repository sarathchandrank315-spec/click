package com.click.aifa.data.user

import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long   // returns user id

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMembers(members: List<FamilyMemberEntity>)
    @Update
    suspend fun updateFamilyMembers(members: List<FamilyMemberEntity>)
    @Query("SELECT * FROM users WHERE phone = :phone")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM family_members WHERE userId = :userId")
    suspend fun getFamilyMembers(userId: Int): List<FamilyMemberEntity>

    @Transaction
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserWithFamily(id: Int): UserWithFamily?
}
