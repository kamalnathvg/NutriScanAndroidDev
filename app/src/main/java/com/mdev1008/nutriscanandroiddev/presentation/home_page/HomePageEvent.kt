package com.mdev1008.nutriscanandroiddev.presentation.home_page

import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails

sealed class HomePageEvent {
    data class GetSearchHistory(val userId: String): HomePageEvent()
    data object GetUserDetails: HomePageEvent()
}