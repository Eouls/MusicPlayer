package com.example.musicplayer.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM UserTable")
    fun getUsers(): List<User>

    @Query("SELECT * FROM UserTable WHERE id = :id")
    fun getUsers(id: Int): User

}