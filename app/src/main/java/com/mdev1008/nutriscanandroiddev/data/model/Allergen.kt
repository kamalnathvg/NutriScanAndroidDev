package com.mdev1008.nutriscanandroiddev.data.model

import com.mdev1008.nutriscanandroiddev.utils.AppResources


enum class Allergen(val heading: String,val allergenStrings: List<String>) {
    GLUTEN("Gluten", AppResources.GLUTEN_ALLERGENS),
    CRUSTACEANS("Crustaceans", AppResources.CRUSTACEANS_ALLERGENS),
    EGG("Egg", AppResources.EGG_ALLERGENS),
    FISH("Fish", AppResources.FISH_ALLERGENS),
    RED_CAVIAR("Red Caviar", AppResources.RED_CAVIAR_ALLERGENS),
    ORANGE("Orange", AppResources.ORANGE_ALLERGENS),
    KIWI("Kiwi", AppResources.KIWI_ALLERGENS),
    BANANA("Banana", AppResources.BANANA_ALLERGENS),
    PEACH("Peach", AppResources.PEACH_ALLERGENS),
    APPLE("Apple", AppResources.APPLE_ALLERGENS),
    BEEF("Beef", AppResources.BEEF_ALLERGENS),
    PORK("Pork", AppResources.PORK_ALLERGENS),
    CHICKEN("Chicken", AppResources.CHICKEN_ALLERGENS),
    YAMAIMO("Yamaimo", AppResources.YAMAIMO_ALLERGENS),
    GELATIN("Gelatin", AppResources.GELATIN_ALLERGENS),
    MATSUTAKE("Matsutake", AppResources.MATSUTAKE_ALLERGENS),
    PEANUTS("Peanuts", AppResources.PEANUT_ALLERGENS),
    SOYBEANS("Soybeans", AppResources.SOY_ALLERGENS),
    MILK("Milk", AppResources.MILK_ALLERGENS),
    NUTS("Nuts", AppResources.NUTS_ALLERGENS),
    CELERY("Celery", AppResources.CELERY_ALLERGENS),
    MUSTARD("Mustard", AppResources.MUSTARD_ALLERGENS),
    SESAME("Sesame", AppResources.SESAME_ALLERGENS),
    SULPHUR_DIOXIDE_AND_SULPHITES("Sulphur Dioxide and Sulphites", AppResources.SULPHUR_DIOXIDE_AND_SULPHIDE_ALLERGENS),
    LUPIN("Lupin", AppResources.LUPIN_ALLERGENS),
    MOLLUSCS("Molluscs", AppResources.MOLLUSCS_ALLERGENS)
}



fun String.toAllergen(): Allergen?{
    return Allergen.entries.find {
        this in it.allergenStrings
    }
}

fun List<String>.toAllergens(): List<Allergen>{
    val allergenMutableList = mutableListOf<Allergen>()
    this.forEach {allergenString ->
        allergenString.toAllergen()?.let { allergen ->
            allergenMutableList.add(allergen)
        }
    }
    return allergenMutableList
}

