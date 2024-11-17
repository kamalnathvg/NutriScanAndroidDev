package com.mdev1008.nutriscanandroiddev.presentation.profile_page

import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails

sealed class ProfilePageEvent {
    data object GetUserDetails: ProfilePageEvent()
    data class UpdateUserDetails(val userProfileDetails: UserProfileDetails): ProfilePageEvent()
}