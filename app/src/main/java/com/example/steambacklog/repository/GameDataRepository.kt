package com.example.steambacklog.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.steambacklog.database.GameDataDao
import com.example.steambacklog.database.GameDataRoomDatabase
import com.example.steambacklog.model.GameData

class GameDataRepository(context: Context){
    private val gameDataDao: GameDataDao

    init {
        val database = GameDataRoomDatabase.getDatabase(context)
        gameDataDao = database!!.gameDataDao()
    }

    fun getAllGameData(): LiveData<List<GameData>> {
        return gameDataDao.getAllGameData()
    }

    suspend fun insertGameData(gameData: GameData){
        gameDataDao.insertGameData(gameData)
    }

    suspend fun updateGameData(gameData: GameData){
        gameDataDao.updateGameData(gameData)
    }

    suspend fun deleteGameData(gameData: GameData) {
        gameDataDao.deleteGameData(gameData)
    }

    suspend fun deleteAllGameData() {
        gameDataDao.deleteAllGameData()
    }

    fun getGameDataEntry(appID: Int, userID: Long): LiveData<GameData>?{
        return gameDataDao.getGameDataByID(appID, userID)
    }

    fun getGameDataEntryOnce(appID: Int, userID: Long): GameData?{
        return gameDataDao.getGameDataByIDOnce(appID, userID)
    }
}