package com.mdev1008.nutriscanandroiddev.models.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mdev1008.nutriscanandroiddev.models.remote.User
import java.time.LocalDateTime

@Entity(tableName = "search_history",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            childColumns = ["user_id"],
            parentColumns = ["user_id"]
        )
    ])
data class SearchHistoryItem(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "item_id") val id: Int? = null,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "product_id") val productId: String,
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "product_brand") val productBrand: String,
    @ColumnInfo(name = "health_category") val healthCategory: HealthCategory,
    @ColumnInfo(name = "time_stamp") val timeStamp: LocalDateTime
)







