package com.mdev1008.nutriscanandroiddev.pages.homepage

import com.mdev1008.nutriscanandroiddev.models.remote.UserAllergen
import com.mdev1008.nutriscanandroiddev.models.remote.UserDietaryPreference
import com.mdev1008.nutriscanandroiddev.models.remote.UserDietaryRestriction
import com.mdev1008.nutriscanandroiddev.models.remote.UserProfileDetails

sealed class HomePageEvent {
    data object SignOut : HomePageEvent()
    data class FetchSearchHistory(val userId: String): HomePageEvent()
    data class FetchProductDetails(val productId: String): HomePageEvent()
    data class UpdateUserDetails(val userDetails: UserProfileDetails): HomePageEvent()
    data object GetUserDetails: HomePageEvent()
}