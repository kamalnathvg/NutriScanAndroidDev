package com.mdev1008.nutriscanandroiddev.presentation.register_page

sealed class RegisterPageEvent {
    data class RegisterWithUserNamePassword(val userName: String, val password: String): RegisterPageEvent()
}