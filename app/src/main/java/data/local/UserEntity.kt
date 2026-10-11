package com.example.onestopfootball.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username_key"], unique = true),
        Index(value = ["email_key"], unique = true)
    ]
)
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val username: String,

    @ColumnInfo(name = "username_key")
    val usernameKey: String,

    val email: String,

    @ColumnInfo(name = "email_key")
    val emailKey: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "password_salt")
    val passwordSalt: String,

    @ColumnInfo(name = "password_algorithm")
    val passwordAlgorithm: String,

    @ColumnInfo(name = "password_iterations")
    val passwordIterations: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)