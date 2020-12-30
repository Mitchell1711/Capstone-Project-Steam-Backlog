package com.example.steambacklog.API

import com.example.steambacklog.model.Library
import com.example.steambacklog.model.Response
import retrofit2.http.GET

interface SteamAPIService {
    // The GET method needed to retrieve a random number trivia.
    @GET("/IPlayerService/GetOwnedGames/v1/?key=7BE4749AE70DF6436AF3A189A6A1A9B6&steamid=76561198257218665&include_appinfo=true&include_played_free_games=true&format=json")
    suspend fun getSteamLibrary(): Response
}