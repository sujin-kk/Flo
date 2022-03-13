package com.example.flo.Fragment

import androidx.room.*
import com.example.flo.Class.Song

@Dao
interface SongDao {

    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Delete
    fun delete(song: Song)

    @Query("SELECT * FROM SongTable")
    fun getSongs(): List<Song>

    @Query("SELECT * FROM SongTable WHERE id = :id")
    fun getSong(id: Int): Song

    @Query("UPDATE SongTable SET isLike= :isLike WHERE id = :id")
    fun updateIsLikeById(isLike: Boolean, id: Int)

    @Query("SELECT * FROM SongTable WHERE isLike = :isLike")
    fun getLikedSongs(isLike: Boolean): List<Song>

    @Query("SELECT * FROM SongTable WHERE albumIdx = :albumIdx")
    fun getSongsInAlbum(albumIdx: Int): List<Song>

    @Query("SELECT * FROM SongTable WHERE albumIdx = :albumIdx AND id = (SELECT MIN(id) FROM SongTable)")
    fun getFirstSongInAlbum(albumIdx: Int): Song
}