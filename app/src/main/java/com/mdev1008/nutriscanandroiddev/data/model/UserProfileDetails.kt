package com.mdev1008.nutriscanandroiddev.data.model

data class UserProfileDetails(
    val userDetails: User,
    val userPreferences: List<UserDietaryPreference>,
    val userRestrictions: List<UserDietaryRestriction>,
    val userAllergen: List<UserAllergen>
)
