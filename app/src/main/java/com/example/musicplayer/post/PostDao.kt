package com.example.musicplayer.post

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PostDao {
    @Insert
    fun insert(post: Post)

    @Update
    fun update(post: Post)

    @Delete
    fun delete(post: Post)

    @Query("SELECT * FROM PostTable")
    fun getPosts(): List<Post>

    @Query("SELECT * FROM PostTable WHERE id = :id")
    fun getPost(id: Int): Post

}