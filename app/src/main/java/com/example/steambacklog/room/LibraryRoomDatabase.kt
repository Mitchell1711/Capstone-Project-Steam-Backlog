package com.example.steambacklog.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.steambacklog.model.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(entities = [Response::class], version = 1, exportSchema = false)
@TypeConverters(com.example.steambacklog.room.TypeConverters::class)
abstract class LibraryRoomDatabase : RoomDatabase() {

    abstract fun libraryDao(): LibraryDao

    companion object {
        private const val DATABASE_NAME = "GAME_DATABASE"

        @Volatile
        private var INSTANCE: LibraryRoomDatabase? = null

        fun getDatabase(context: Context): LibraryRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(LibraryRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                                context.applicationContext,
                                LibraryRoomDatabase::class.java, DATABASE_NAME
                        )
                                .fallbackToDestructiveMigration()
                                .addCallback(object : RoomDatabase.Callback() {
                                    override fun onCreate(db: SupportSQLiteDatabase) {
                                        super.onCreate(db)
                                        INSTANCE?.let { database ->
                                            CoroutineScope(Dispatchers.IO).launch {
                                                //database.libraryDao().updateLibrary()
                                            }
                                        }
                                    }
                                })
                                .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}