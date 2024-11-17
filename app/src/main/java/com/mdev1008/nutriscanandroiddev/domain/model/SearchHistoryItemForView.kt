package com.mdev1008.nutriscanandroiddev.domain.model

import com.mdev1008.nutriscanandroiddev.data.model.HealthCategory
import com.mdev1008.nutriscanandroiddev.data.model.SearchHistoryItem
import java.time.LocalDateTime

data class SearchHistoryItemForView(
    val productName: String,
    val productId: String,
    val productBrand: String?,
    val imageUrl: String,
    val healthCategory: HealthCategory,
    val lastScanned: LocalDateTime
)

fun SearchHistoryItem.toSearchHistoryForView(): SearchHistoryItemForView{
    return SearchHistoryItemForView(
        productName = this.productName,
        productId = this.productId,
        productBrand = this.productBrand,
        imageUrl = this.imageUrl,
        healthCategory = this.healthCategory,
        lastScanned = this.timeStamp
    )
}