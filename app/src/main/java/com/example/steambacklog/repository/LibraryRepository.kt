package com.example.steambacklog.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.steambacklog.API.SteamAPI
import com.example.steambacklog.API.SteamAPIService
import com.example.steambacklog.model.Library
import com.example.steambacklog.model.Response
import kotlinx.coroutines.withTimeout

class LibraryRepository {
    private val steamAPIService: SteamAPIService = SteamAPI.createApi()

    private val _library: MutableLiveData<Response> = MutableLiveData()

    /**
     * Expose non MutableLiveData via getter
     * Encapsulation :)
     */
    val library: LiveData<Response>
        get() = _library

    /**
     * suspend function that calls a suspend function from the steamAPI call
     */
    suspend fun getSteamLibrary(userID : Long)  {
        try {
            //timeout the request after 5 seconds
            val result = withTimeout(5_000) {
                steamAPIService.getSteamLibrary(key = "7BE4749AE70DF6436AF3A189A6A1A9B6", userID, include_appinfo = true, include_played_free_games = true, format = "json")
            }

            _library.value = result
        } catch (error: Throwable) {
            throw LibraryRefreshError("Unable to refresh Library", error)
        }
    }

    class LibraryRefreshError(message: String, cause: Throwable) : Throwable(message, cause)
}
