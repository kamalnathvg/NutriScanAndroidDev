package com.mdev1008.nutriscanandroiddev.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.mdev1008.nutriscanandroiddev.data.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user WHERE username = :userName")
    fun getUser(userName: String): List<User>

    @Query("SELECT * FROM user WHERE user_id = :id")
    fun getUserById(id: Int): List<User>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun createUser(user: User)

    @Upsert()
    fun updateUser(user: User)

}