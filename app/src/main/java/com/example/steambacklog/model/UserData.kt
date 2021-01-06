package com.example.steambacklog.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userTable")
data class UserData(
    @ColumnInfo(name = "userid")
    var userID: Long,

    @ColumnInfo(name = "completionstatus")
    var completionStatus: Completion,

    @ColumnInfo(name = "librarydata")
    var libraryData: ArrayList<LibraryData>,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null
)