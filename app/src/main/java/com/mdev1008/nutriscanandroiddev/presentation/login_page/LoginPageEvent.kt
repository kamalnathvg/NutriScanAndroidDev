package com.mdev1008.nutriscanandroiddev.presentation.login_page

sealed class LoginPageEvent {
    data class LoginWithUserNamePassword(val email: String, val password: String): LoginPageEvent()
}