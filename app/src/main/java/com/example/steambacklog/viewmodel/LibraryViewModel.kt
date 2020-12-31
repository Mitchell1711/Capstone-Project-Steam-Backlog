package com.example.steambacklog.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.steambacklog.model.Response
import com.example.steambacklog.repository.LibraryRepository
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val libraryRepository = LibraryRepository()

    val library = libraryRepository.library

    //livedata observers for error codes
    private val _errorText: MutableLiveData<String> = MutableLiveData()

    val errorText: LiveData<String>
        get() = _errorText
//
//    //livedata observers for library instances
//    val libraryItems: LiveData<List<Response>>
//        get() = _libraryItems
//
//    private val _libraryItems = MutableLiveData<List<Response>>().apply {
//        value = libraryRepository.getSteamLibrary()
//    }


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
