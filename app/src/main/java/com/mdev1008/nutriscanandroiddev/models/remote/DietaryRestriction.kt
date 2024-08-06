package com.mdev1008.nutriscanandroiddev.models.remote

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