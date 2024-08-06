package com.mdev1008.nutriscanandroiddev.models.data

import com.mdev1008.nutriscanandroiddev.models.remote.NutrientPreference

data class NutrientForView(
    val nutrientType: NutrientType,
    val contentPerHundredGrams: Number,
    val description: String,
    val pointsLevel: PointsLevel,
    val nutrientCategory: NutrientCategory,
    val healthCategory: HealthCategory,
    val servingUnit: String,
)

