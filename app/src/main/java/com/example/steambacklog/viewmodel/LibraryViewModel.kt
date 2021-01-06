package com.example.steambacklog.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.steambacklog.model.Response
import com.example.steambacklog.repository.LibraryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val libraryRepository = LibraryRepository(application.applicationContext)

    val library = libraryRepository.library

    private val mainScope = CoroutineScope(Dispatchers.Main)

    //livedata observers for error codes
    private val _errorText: MutableLiveData<String> = MutableLiveData()

    val errorText: LiveData<String>
        get() = _errorText

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
