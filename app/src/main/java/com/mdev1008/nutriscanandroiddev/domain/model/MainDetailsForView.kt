package com.mdev1008.nutriscanandroiddev.domain.model

import com.mdev1008.nutriscanandroiddev.data.model.DietaryRestriction
import com.mdev1008.nutriscanandroiddev.data.model.HealthCategory
import com.mdev1008.nutriscanandroiddev.data.model.SearchHistoryItem
import java.time.LocalDateTime

data class MainDetailsForView(
    val productId: String,
    val imageUrl: String? = null,
    val productName: String? = null,
    val productBrand: String? = null,
    val palmOilStatus: DietaryRestriction = DietaryRestriction.PALM_OIL_STATUS_UNKNOWN,
    val veganStatus: DietaryRestriction = DietaryRestriction.VEGAN_STATUS_UNKNOWN,
    val vegetarianStatus: DietaryRestriction = DietaryRestriction.VEGETARIAN_STATUS_UNKNOWN,
    val healthCategory: HealthCategory = HealthCategory.UNKNOWN
)


fun MainDetailsForView.toSearchHistoryItem(userId: Int): SearchHistoryItem {
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