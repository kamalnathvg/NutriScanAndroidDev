package com.mdev1008.nutriscanandroiddev.models.remote

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mdev1008.nutriscanandroiddev.models.data.SearchHistoryItem

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history WHERE user_id = :userId ORDER BY time_stamp DESC")
    fun getUserSearchHistory(userId: Int): List<SearchHistoryItem>

    @Upsert()
    fun addItem(searchHistoryItem: SearchHistoryItem)

}