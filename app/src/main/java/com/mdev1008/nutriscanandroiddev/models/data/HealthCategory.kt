package com.mdev1008.nutriscanandroiddev.models.data

enum class HealthCategory(val description: String){
    HEALTHY("Healthy"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor"),
    BAD("Bad"),
    UNKNOWN("Unknown")
}


fun String?.getHealthCategory(): HealthCategory{
    return when(this){
        "a" -> HealthCategory.HEALTHY
        "b" -> HealthCategory.GOOD
        "c" -> HealthCategory.FAIR
        "d" -> HealthCategory.POOR
        "e" -> HealthCategory.BAD
        else -> HealthCategory.UNKNOWN
    }
}



