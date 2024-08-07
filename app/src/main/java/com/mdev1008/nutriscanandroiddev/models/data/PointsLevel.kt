package com.mdev1008.nutriscanandroiddev.models.data

import com.mdev1008.nutriscanandroiddev.models.remote.NutrientPreference

enum class PointsLevel(val description: String){
    TOO_LOW ("very low"),
    LOW ("low"),
    MODERATE ("moderate"),
    HIGH ("high"),
    TOO_HIGH ("very high"),
    UNKNOWN ("unknown")
}

fun Int?.getPointsLevel(nutrientType: NutrientType): PointsLevel {
    if (this == null){
        return PointsLevel.UNKNOWN
    }
    when(nutrientType){
        NutrientType.ENERGY,
        NutrientType.SATURATES,
        NutrientType.SUGAR,
        NutrientType.SODIUM -> {
            return when(this){
                0,1 -> PointsLevel.TOO_LOW
                2,3 -> PointsLevel.LOW
                4,5 -> PointsLevel.MODERATE
                6,7 -> PointsLevel.HIGH
                8,9,10 -> PointsLevel.TOO_HIGH
                else -> PointsLevel.UNKNOWN
            }
        }
        NutrientType.PROTEIN,
        NutrientType.FIBRE ,
        NutrientType.FRUITS_VEGETABLES_AND_NUTS -> {
            return when(this){
                0 -> PointsLevel.TOO_LOW
                1 -> PointsLevel.LOW
                2,3 -> PointsLevel.MODERATE
                4 -> PointsLevel.HIGH
                5 -> PointsLevel.TOO_HIGH
                else -> PointsLevel.UNKNOWN
            }
        }
    }
}


fun PointsLevel.getHealthCategory(nutrientType: NutrientType): HealthCategory{
    when(nutrientType){
        NutrientType.ENERGY,
        NutrientType.SATURATES,
        NutrientType.SUGAR,
        NutrientType.SODIUM -> {
            return when(this){
                PointsLevel.TOO_LOW -> HealthCategory.HEALTHY
                PointsLevel.LOW -> HealthCategory.GOOD
                PointsLevel.MODERATE -> HealthCategory.FAIR
                PointsLevel.HIGH -> HealthCategory.POOR
                PointsLevel.TOO_HIGH -> HealthCategory.BAD
                PointsLevel.UNKNOWN -> HealthCategory.UNKNOWN
            }
        }
        NutrientType.PROTEIN,
        NutrientType.FIBRE ,
        NutrientType.FRUITS_VEGETABLES_AND_NUTS -> {
            return when(this){
                PointsLevel.TOO_LOW,
                PointsLevel.LOW ,
                PointsLevel.MODERATE -> HealthCategory.GOOD

                PointsLevel.HIGH,
                PointsLevel.TOO_HIGH,
                PointsLevel.UNKNOWN -> HealthCategory.HEALTHY
            }
        }
    }
}

fun PointsLevel.toNutrientPreference(): NutrientPreference?{
    return when(this){
        PointsLevel.TOO_LOW,
        PointsLevel.LOW -> NutrientPreference.LOW
        PointsLevel.MODERATE -> NutrientPreference.MODERATE
        PointsLevel.HIGH,
        PointsLevel.TOO_HIGH -> NutrientPreference.HIGH
        PointsLevel.UNKNOWN -> null
    }
}