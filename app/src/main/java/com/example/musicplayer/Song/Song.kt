package com.example.musicplayer.Song

import androidx.room.Entity
import androidx.room.PrimaryKey

// 제목, 가수, 사진, 재생시간, 현재 재생시간, isPlaying(재생되고 있는지)
@Entity(tableName = "SongTable")
data class Song(
    var title : String = "", // 제목
    var singer : String = "",  // 가수
    var second : Int = 0, // 현재 재생시간
    var playTime : Int = 0, // 재생시간
    var isPlaying : Boolean = false, // 재생되고 있는 지
    var music : String = "", // 음원 파일
    var coverImg: Int? = null, // 앨범 이미지
    var isLike : Boolean = false, // 좋아요
    var albumIdx : Int = 0 // 앨범 아이디
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

