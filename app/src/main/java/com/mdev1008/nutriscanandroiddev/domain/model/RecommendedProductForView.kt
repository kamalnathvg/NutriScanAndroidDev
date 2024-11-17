package com.mdev1008.nutriscanandroiddev.domain.model

import com.mdev1008.nutriscanandroiddev.data.model.HealthCategory
import com.mdev1008.nutriscanandroiddev.data.model.RecommendedProductDto
import com.mdev1008.nutriscanandroiddev.data.model.getHealthCategory

data class RecommendedProductForView(
    val productId: String,
    val productName: String,
    val brand: String,
    val imageUrl: String,
    val healthCategory: HealthCategory
)



fun RecommendedProductDto.parseForView(): RecommendedProductForView{
    val healthCategory = this.nutriscoreGrade.getHealthCategory()
    return RecommendedProductForView(
        productId = this.code ?: "",
        productName = this.productName ?: "",
        brand = this.brands ?: "",
        imageUrl = this.imageUrl ?: "",
        healthCategory = healthCategory
    )
}
