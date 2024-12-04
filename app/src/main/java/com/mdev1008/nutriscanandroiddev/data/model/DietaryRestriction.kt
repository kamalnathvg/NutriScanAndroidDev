package com.mdev1008.nutriscanandroiddev.data.model

enum class DietaryRestriction(val heading: String,val response: String,val conclusionString: String) {
    VEGAN("Vegan","en:vegan", "Vegan"),
    NON_VEGAN("Non-Vegan","en:non-vegan", "Non-Vegan"),
    VEGAN_STATUS_UNKNOWN("Vegan Status Unknown","en:vegan-status-unknown", "Vegan status is unknown"),
    PALM_OIL("Palm Oil","en:palm-oil", "contains Palm Oil"),
    PALM_OIL_FREE("Palm Oil Free","en:palm-oil-free", "Palm Oil free"),
    PALM_OIL_STATUS_UNKNOWN("Palm Oil Status Unknown","en:palm-oil-content-unknown", "Palm Oil content is unknown"),
    VEGETARIAN("Vegetarian","en:vegetarian", "Vegetarian"),
    NON_VEGETARIAN("Non-Vegetarian","en:non-vegetarian", "Non-Vegetarian"),
    VEGETARIAN_STATUS_UNKNOWN("Vegetarian Status Unknown","en:vegetarian-status-unknown", "Vegetarian status is unknown")
}


fun String.toDietaryRestriction(): DietaryRestriction?{
    return DietaryRestriction.entries.find {
        it.response == this
    }
}

fun dietaryRestrictionForProfilePage(): List<DietaryRestriction>{
    return mutableListOf<DietaryRestriction>(
        DietaryRestriction.VEGAN,
        DietaryRestriction.VEGETARIAN,
        DietaryRestriction.PALM_OIL_FREE
    )
}

fun DietaryRestriction.isVeganRestriction(): Boolean{
    val restrictions = mutableListOf(
        DietaryRestriction.VEGAN,
        DietaryRestriction.NON_VEGAN,
        DietaryRestriction.VEGAN_STATUS_UNKNOWN
    )
    return this in restrictions
}

fun DietaryRestriction.isVegetarianRestriction(): Boolean{
    val restrictions = mutableListOf(
        DietaryRestriction.VEGETARIAN,
        DietaryRestriction.NON_VEGETARIAN,
        DietaryRestriction.VEGETARIAN_STATUS_UNKNOWN
    )
    return this in restrictions
}

fun DietaryRestriction.isPalmOilRestriction(): Boolean{
    val restrictions = mutableListOf(
        DietaryRestriction.PALM_OIL,
        DietaryRestriction.PALM_OIL_FREE,
        DietaryRestriction.PALM_OIL_STATUS_UNKNOWN
    )
    return this in restrictions
}

fun List<DietaryRestriction>.getVeganStatus(): DietaryRestriction{
    return this.filter{ it.isVeganRestriction() }.getOrNull(0) ?: DietaryRestriction.VEGAN_STATUS_UNKNOWN
}
fun List<DietaryRestriction>.getVegetarianStatus(): DietaryRestriction{
    return this.filter{ it.isVegetarianRestriction() }.getOrNull(0) ?: DietaryRestriction.VEGETARIAN_STATUS_UNKNOWN
}
fun List<DietaryRestriction>.getPalmOilStatus(): DietaryRestriction{
    return this.filter{ it.isPalmOilRestriction() }.getOrNull(0) ?: DietaryRestriction.PALM_OIL_STATUS_UNKNOWN
}





fun DietaryRestriction.isSameCategory(dietaryRestriction: DietaryRestriction): Boolean{
    return (this.isVeganRestriction() && dietaryRestriction.isVeganRestriction())
            || (this.isVegetarianRestriction() && dietaryRestriction.isVegetarianRestriction())
            || (this.isPalmOilRestriction() && dietaryRestriction.isPalmOilRestriction())
}
