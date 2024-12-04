package com.mdev1008.nutriscanandroiddev.domain.model

import com.mdev1008.nutriscanandroiddev.data.model.HealthCategory
import com.mdev1008.nutriscanandroiddev.data.model.Product
import com.mdev1008.nutriscanandroiddev.data.model.getHealthCategory

data class ScanItemForView(
    val productId: String,
    val productName: String,
    val productBrand: String,
    val imageUrl: String,
    val healthCategory: HealthCategory,
    val additivesCount: String,
    val allergenCount: String,
)



fun Product.toScanItemForView(): ScanItemForView{
    val healthCategory = this.nutriScoreGrade.getHealthCategory()
    val additivesCount = this.additivesTags?.size ?: "Unknown"
    val allergenCount = this.allergensHierarchy?.size ?: "Unknown"
    return ScanItemForView(
        productId = this.productId,
        productName = this.productName,
        productBrand = this.brand ?: "Unknown",
        imageUrl = this.imageUrl ?: "",
        healthCategory = healthCategory,
        additivesCount = "Additives: $additivesCount", //TODO: Needs to be fixed
        allergenCount = "Allergens: $allergenCount" //TODO: Needs to be fixed
    )
}