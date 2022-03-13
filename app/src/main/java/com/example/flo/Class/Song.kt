package com.example.flo.Class

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "SongTable")
data class Song(
    var title : String = "", // 제목
    var singer : String = "", // 가수
    val isTitle : Boolean = false,
    var playTime : Int=0, // 재생시간
    var isPlaying : Boolean = false, // 재생 여부
    var music : String = "", // 음악
    var second : Int = 0, // 현재 재생시간
    var coverImg: Int? =null,
    var isLike: Boolean = false,
    var albumIdx: Int=0
) : Serializable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
