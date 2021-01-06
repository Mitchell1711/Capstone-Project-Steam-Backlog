package com.example.steambacklog.model

import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class Library(
        @ColumnInfo(name = "game_count") @SerializedName("game_count") val game_count : Int,
        @ColumnInfo(name = "games") @SerializedName("games") val games : List<Games>
)