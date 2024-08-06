package com.mdev1008.nutriscanandroiddev.pages.authpages

sealed class AuthEvent {
    data class RegisterWithUserNamePassword(val userName: String, val password: String): AuthEvent()
    data class SignInWithUserNamePassword(val userName: String, val password: String): AuthEvent()
}