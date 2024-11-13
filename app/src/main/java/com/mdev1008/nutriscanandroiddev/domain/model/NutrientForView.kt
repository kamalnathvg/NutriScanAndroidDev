package com.mdev1008.nutriscanandroiddev.domain.model

import com.mdev1008.nutriscanandroiddev.data.model.HealthCategory
import com.mdev1008.nutriscanandroiddev.data.model.NutrientCategory
import com.mdev1008.nutriscanandroiddev.data.model.NutrientType
import com.mdev1008.nutriscanandroiddev.data.model.PointsLevel

data class NutrientForView(
    val nutrientType: NutrientType,
    val contentPerHundredGrams: Number,
    val description: String,
    val pointsLevel: PointsLevel,
    val nutrientCategory: NutrientCategory,
    val healthCategory: HealthCategory,
    val servingUnit: String,
)

