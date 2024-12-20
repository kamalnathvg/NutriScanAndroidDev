package com.mdev1008.nutriscanandroiddev.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mdev1008.nutriscanandroiddev.data.model.UserDietaryRestriction

@Dao
interface UserDietaryRestrictionDao{

    @Query("SELECT * FROM user_dietary_restriction WHERE user_id = :userId")
    fun getUserRestrictions(userId: Int): List<UserDietaryRestriction>

    @Query("DELETE FROM user_dietary_restriction WHERE user_id = :userId")
    fun deleteUserRestrictions(userId: Int)

    @Upsert
    fun addUserRestriction(userRestrictions: List<UserDietaryRestriction>)
}