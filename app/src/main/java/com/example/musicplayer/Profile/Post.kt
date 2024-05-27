package com.example.musicplayer.Profile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PostTable")
data class Post(
        var postImg: Int? = null,
        val title : String = "",
        val content : String = ""
){
        @PrimaryKey(autoGenerate = true) var id: Int = 0
}