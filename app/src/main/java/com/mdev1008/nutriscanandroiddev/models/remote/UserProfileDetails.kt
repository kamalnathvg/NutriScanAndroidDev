package com.mdev1008.nutriscanandroiddev.models.remote

data class UserProfileDetails(
    val userDetails: User,
    val userPreferences: List<UserDietaryPreference>,
    val userRestrictions: List<UserDietaryRestriction>,
    val userAllergen: List<UserAllergen>
)
