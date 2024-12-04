package com.mdev1008.nutriscanandroiddev.presentation.home_page

sealed class HomePageEvent {
    data object GetSearchHistory : HomePageEvent()
    data object GetUserDetails: HomePageEvent()
}