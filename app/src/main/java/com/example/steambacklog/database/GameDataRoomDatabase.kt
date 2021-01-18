package com.example.steambacklog.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.steambacklog.model.GameData

@Database(entities = [GameData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GameDataRoomDatabase : RoomDatabase() {

    abstract fun gameDataDao(): GameDataDao

    companion object {
        private const val DATABASE_NAME = "GAMEDATA_DATABASE"

        @Volatile
        private var INSTANCE: GameDataRoomDatabase? = null

        fun getDatabase(context: Context): GameDataRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(GameDataRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, GameDataRoomDatabase::class.java, DATABASE_NAME).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}