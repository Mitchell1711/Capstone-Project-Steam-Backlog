package com.example.steambacklog.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.steambacklog.model.Completion
import com.example.steambacklog.model.GameData
import com.example.steambacklog.repository.GameDataRepository
import com.example.steambacklog.repository.LibraryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val mainScope = CoroutineScope(Dispatchers.Main)

    private val libraryRepository = LibraryRepository()
    val library = libraryRepository.library

    private val gameDataRepository = GameDataRepository(application.applicationContext)

    fun gameData(appID: Int, userID: Long) = gameDataRepository.getGameDataEntry(appID, userID)
    fun gameDataOnce(appID: Int, userID: Long) = gameDataRepository.getGameDataEntryOnce(appID, userID)

    //livedata observers for error codes
    private val _errorText: MutableLiveData<String> = MutableLiveData()

    val errorText: LiveData<String>
        get() = _errorText

    fun insertGameData(appID: Int, userID: Long, completion: Completion){
        val newGameData = GameData(
            appID = appID,
            userID = userID,
            completion = completion,
            rating = 0.toFloat(),
            note = ""
        )

        mainScope.launch {
            withContext(Dispatchers.IO) {
                gameDataRepository.insertGameData(newGameData)
            }
        }
    }

    fun updateGameData(appID: Int, userID: Long, completion: Completion?, rating: Float?, note: String?){
        val newGameData = GameData(
            appID = appID,
            userID = userID,
            completion = completion,
            rating = rating,
            note = note
        )

        mainScope.launch {
            withContext(Dispatchers.IO) {
                gameDataRepository.updateGameData(newGameData)
            }
        }
    }

    fun deleteGameData(gameData: GameData){
        mainScope.launch {gameDataRepository.deleteGameData(gameData) }
    }

    fun deleteAllGameData() {
        mainScope.launch { gameDataRepository.deleteAllGameData() }
    }


    /**
     * The viewModelScope is bound to Dispatchers.Main and will automatically be cancelled when the ViewModel is cleared.
     * Extension method of lifecycle-viewmodel-ktx library
     */
    fun getSteamGames(userID: Long) {
        viewModelScope.launch {
            try {
                //the triviaRepository sets it's own livedata property
                //our own library LiveData property points to te one in that repository
                libraryRepository.getSteamLibrary(userID)
            } catch (error: LibraryRepository.LibraryRefreshError) {
                _errorText.value = error.message
                Log.e("Library error", error.cause.toString())
            }
        }
    }
}
