package com.mdev1008.nutriscanandroiddev.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mdev1008.nutriscanandroiddev.data.model.UserAllergen

@Dao
interface UserAllergenDao {

    @Query("SELECT * FROM user_allergen WHERE user_id = :userId")
    fun getUserAllergens(userId: Int): List<UserAllergen>

    @Query("DELETE FROM user_allergen WHERE user_id = :userId")
    fun deleteUserAllergens(userId: Int)

    @Upsert
    fun addUserAllergens(userAllergens: List<UserAllergen>)
}