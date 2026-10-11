package com.example.onestopfootball.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: UserEntity): Long

    @Query(
        """
        SELECT * FROM users
        WHERE username_key = :usernameKey
        LIMIT 1
        """
    )
    suspend fun findByUsername(usernameKey: String): UserEntity?

    @Query(
        """
        SELECT * FROM users
        WHERE email_key = :emailKey
        LIMIT 1
        """
    )
    suspend fun findByEmail(emailKey: String): UserEntity?
}