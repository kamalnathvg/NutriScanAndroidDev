package com.mdev1008.nutriscanandroiddev.models.data

import java.time.LocalDateTime

data class MainDetailsForView(
    val productId: String,
    val imageUrl: String? = null,
    val productName: String? = null,
    val productBrand: String? = null,
    val healthCategory: HealthCategory = HealthCategory.UNKNOWN
)


fun MainDetailsForView.toSearchHistoryItem(userId: Int): SearchHistoryItem{
    return SearchHistoryItem(
        userId = userId,
        productId = this.productId,
        productName = this.productName ?: "",
        imageUrl = this.imageUrl ?: "",
        productBrand = this.productBrand ?: "",
        healthCategory = this.healthCategory,
        timeStamp = LocalDateTime.now()
    )
}