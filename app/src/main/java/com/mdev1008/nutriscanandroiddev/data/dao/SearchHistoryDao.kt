package com.mdev1008.nutriscanandroiddev.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mdev1008.nutriscanandroiddev.data.model.SearchHistoryItem

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history WHERE user_id = :userId ORDER BY time_stamp DESC")
    fun getUserSearchHistory(userId: Int): List<SearchHistoryItem>

    @Upsert()
    fun addItem(searchHistoryItem: SearchHistoryItem)

}