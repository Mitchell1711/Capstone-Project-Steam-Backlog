package com.example.steambacklog.model

import androidx.room.Entity

@Entity(tableName = "gameDataTable", primaryKeys = ["appID", "userID"])
data class GameData(
        val appID: Int,
        val userID: Long,
        var completion: Completion?,
        var rating: Float?,
        var note: String?,
)