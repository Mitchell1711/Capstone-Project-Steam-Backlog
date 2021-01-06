package com.example.steambacklog.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.steambacklog.model.Response

@Dao
interface LibraryDao {
    @Insert
    suspend fun insertLibrary(response: Response)

    @Query("SELECT * FROM libraryTable LIMIT 1")
    fun getLibrary(): LiveData<Response?>

    @Update
    suspend fun updateLibrary(response: Response)

    @Delete
    suspend fun deleteLibrary(response: Response)

    @Query("DELETE FROM libraryTable")
    suspend fun deleteAllLibraries()
}