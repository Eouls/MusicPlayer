package com.example.musicplayer.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PostTable")
data class Post(
        // var postImg: Int? = null,
        val title : String = "",
        val name : String = "",
        val content : String = "",
        val date : String = "",
        val imagePath: String? = null
){
        @PrimaryKey(autoGenerate = true) var id: Int = 0
}