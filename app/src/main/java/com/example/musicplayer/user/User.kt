package com.example.musicplayer.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserTable")
data class User(
        val coverImgPath: String? = null,
        val profileImgPath: String? = null,
        val blogName: String = "",
        val userName : String = "",
        val introduction : String = "",
){
        @PrimaryKey(autoGenerate = true) var id: Int = 0
}