package com.example.musicplayer.Album

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.musicplayer.Song.Song

@Dao
interface AlbumDao {
    @Insert
    fun insert(album: Album)

    @Update
    fun update(album: Album)

    @Delete
    fun delete(album: Album)

    @Query("SELECT * FROM AlbumTable") // 테이블의 모든 값을 가져와라
    fun getAlbums(): List<Album>

    @Query("SELECT * FROM AlbumTable WHERE id = :id")
    fun getAlbum(id: Int): Album

    @Query("UPDATE AlbumTable SET isLike= :isLike WHERE id = :id")
    fun updateIsLikeById(isLike: Boolean,id: Int)

    @Query("SELECT * FROM AlbumTable WHERE isLike = :isLike")
    fun getLikedAlbums(isLike: Boolean): List<Album>
}