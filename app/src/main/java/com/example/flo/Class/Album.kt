package com.example.flo.Class

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AlbumTable")
data class Album (
    @PrimaryKey(autoGenerate = false) var id: Int = 0, // album의 pk는 임의로
    var title: String? = "",
    var singer: String? = "",
    var coverImg: Int? = null,
)