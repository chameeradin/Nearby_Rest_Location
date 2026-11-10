package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String = "",
    val isGoogleUser: Boolean = false,
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
