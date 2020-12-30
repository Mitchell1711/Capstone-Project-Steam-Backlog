package com.example.steambacklog.model

import com.google.gson.annotations.SerializedName

data class Library(
    @SerializedName("game_count") val game_count : Int,
    @SerializedName("games") val games : List<Games>
)