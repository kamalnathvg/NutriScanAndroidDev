package com.mdev1008.nutriscanandroiddev.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey( autoGenerate = true) @ColumnInfo(name = "user_id") val id: Int? = null,
    @ColumnInfo(name = "username") val userName: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "isProfileCompleted") val isProfileCompleted: Boolean,
)




