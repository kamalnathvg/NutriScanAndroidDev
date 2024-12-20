package com.mdev1008.nutriscanandroiddev.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mdev1008.nutriscanandroiddev.data.model.UserDietaryPreference

@Dao
interface UserDietaryPreferenceDao {

    @Query("SELECT * FROM user_dietary_preference WHERE user_id = :userId")
    fun getUserPreferences(userId: Int): List<UserDietaryPreference>

//    @Query("DELETE FROM user_dietary_preference WHERE user_id = :userId")
//    fun deletePreferences(userId: Int)

    @Upsert
    fun addPreferences(preferences: List<UserDietaryPreference>)
}