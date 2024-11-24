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
    val additivesCount: Int,
    val allergenCount: Int,
)



fun Product.toScanItemForView(): ScanItemForView{
    val healthCategory = this.nutriScoreGrade.getHealthCategory()
    return ScanItemForView(
        productId = this.productId,
        productName = this.productName,
        productBrand = this.brand ?: "Unknown",
        imageUrl = this.imageUrl ?: "",
        healthCategory = healthCategory,
        additivesCount = 5, //TODO: Needs to be fixed
        allergenCount = 3 //TODO: Needs to be fixed
    )
}