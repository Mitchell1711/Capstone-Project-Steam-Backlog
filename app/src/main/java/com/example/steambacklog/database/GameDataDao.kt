package com.example.steambacklog.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.steambacklog.model.GameData
import com.example.steambacklog.ui.SELECTED_GAME

@Dao
interface GameDataDao {
    @Insert
    suspend fun insertGameData(gameData: GameData)

    @Update
    suspend fun updateGameData(gameData: GameData)

    @Query("SELECT * FROM gameDataTable")
    fun getAllGameData(): LiveData<List<GameData>>

    @Query("SELECT * FROM gameDataTable LIMIT 1")
    fun getGameData(): LiveData<GameData>

    @Query("SELECT * FROM gameDataTable WHERE userID= :userID AND appID= :appID")
    fun getGameDataByID(appID: Int, userID: Long): LiveData<GameData>?

    @Query("SELECT * FROM gameDataTable WHERE userID= :userID AND appID= :appID")
    fun getGameDataByIDOnce(appID: Int, userID: Long): GameData?

    @Delete
    suspend fun deleteGameData(gameData: GameData)

    @Query("DELETE FROM gameDataTable")
    suspend fun deleteAllGameData()
}