package com.click.aifa.data.user

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithFamily(
    @Embedded val user: UserEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val familyMembers: List<FamilyMemberEntity>
)
