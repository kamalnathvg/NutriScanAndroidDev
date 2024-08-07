package com.mdev1008.nutriscanandroiddev.models.data

import Nutrients
import com.mdev1008.nutriscanandroiddev.models.remote.NutriScoreData
import com.mdev1008.nutriscanandroiddev.utils.formatForView

enum class NutrientType(val description: String, val heading: String) {
    ENERGY ("energy","Calories"),
    PROTEIN ("protein", "Protein"),
    SATURATES ("saturated fat","Saturated fat"),
    SUGAR ("sugar", "Sugar"),
    FIBRE ("fibre", "Fibre"),
    SODIUM ("salt", "Sodium"),
    FRUITS_VEGETABLES_AND_NUTS("fruits,vegetables and nuts" , "Fruits, Veggies and Nuts")

}

fun NutrientType.getServingUnit(): String{
   return when(this){
        NutrientType.ENERGY -> "Kcal"
        NutrientType.PROTEIN,
        NutrientType.SATURATES,
        NutrientType.SUGAR ,
        NutrientType.FIBRE -> "g"
        NutrientType.SODIUM -> "mg"
        NutrientType.FRUITS_VEGETABLES_AND_NUTS -> "%"
    }
}

fun NutrientType.getNutrientCategory(pointsLevel: PointsLevel): NutrientCategory{
    return when(this){
        NutrientType.ENERGY,
        NutrientType.SATURATES,
        NutrientType.SUGAR,
        NutrientType.SODIUM -> {
            when(pointsLevel){
                PointsLevel.TOO_LOW,
                PointsLevel.LOW,
                PointsLevel.MODERATE -> NutrientCategory.POSITIVE
                PointsLevel.HIGH,
                PointsLevel.TOO_HIGH -> NutrientCategory.NEGATIVE
                PointsLevel.UNKNOWN -> NutrientCategory.UNKNOWN
            }
        }

        NutrientType.PROTEIN,
        NutrientType.FIBRE,
        NutrientType.FRUITS_VEGETABLES_AND_NUTS -> NutrientCategory.POSITIVE
    }
}

fun NutrientType.getDescription(pointsLevel: PointsLevel): String{
    return "${pointsLevel.description} ${this.heading}"
}

fun NutrientType.getContentPerHundredGram(nutrients: Nutrients): Number{
    return when(this){
        NutrientType.ENERGY -> nutrients.energyKcal100g ?: 0
        NutrientType.PROTEIN -> nutrients.proteins100g ?: 0
        NutrientType.SATURATES -> nutrients.saturatedFat100g ?: 0
        NutrientType.SUGAR -> nutrients.sugars100g ?: 0
        NutrientType.FIBRE -> nutrients.fiber100g ?: 0
        NutrientType.SODIUM -> nutrients.sodium100g ?: 0
        NutrientType.FRUITS_VEGETABLES_AND_NUTS -> nutrients.fruitsVegetablesNutsEstimateFromIngredients100g?.formatForView() ?: 0
    }
}

fun NutrientType.getPointsLevel(nutriScoreData: NutriScoreData): PointsLevel{
    return when(this){
        NutrientType.ENERGY -> nutriScoreData.energyPoints
        NutrientType.PROTEIN -> nutriScoreData.proteinsPoints
        NutrientType.SATURATES -> nutriScoreData.saturatedFatPoints
        NutrientType.SUGAR -> nutriScoreData.sugarsPoints
        NutrientType.FIBRE -> nutriScoreData.fiberPoints
        NutrientType.SODIUM -> nutriScoreData.sodiumPoints
        NutrientType.FRUITS_VEGETABLES_AND_NUTS -> nutriScoreData.fruitsVegetablesNutsColzaWalnutOliveOilsPoints
    }.getPointsLevel(this)
}

