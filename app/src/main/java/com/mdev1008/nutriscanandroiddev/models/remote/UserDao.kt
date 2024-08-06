package com.mdev1008.nutriscanandroiddev.models.remote

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE username = :userName")
    fun getUser(userName: String): List<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun createUser(user: User)

    @Upsert()
    fun updateUser(user: User)

}