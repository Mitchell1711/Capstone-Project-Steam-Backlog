package com.example.steambacklog.API

import com.example.steambacklog.model.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SteamAPIService {
    // The GET method needed to retrieve a random number trivia.
    @GET("/IPlayerService/GetOwnedGames/v1")
    suspend fun getSteamLibrary(@Query("key") key: String,
                                @Query("steamid") steamid: Long,
                                @Query("include_appinfo") include_appinfo: Boolean,
                                @Query("include_played_free_games") include_played_free_games: Boolean,
                                @Query("format") format: String): Response
}