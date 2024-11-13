package com.mdev1008.nutriscanandroiddev.presentation.home_page

import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails

sealed class HomePageEvent {
    data object SignOut : HomePageEvent()
    data class FetchSearchHistory(val userId: String): HomePageEvent()
    data class FetchProductDetails(val productId: String): HomePageEvent()
    data class UpdateUserDetails(val userDetails: UserProfileDetails): HomePageEvent()
    data object GetUserDetails: HomePageEvent()
    data object SkipProfilePage: HomePageEvent()
}