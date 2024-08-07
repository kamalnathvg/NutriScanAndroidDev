package com.mdev1008.nutriscanandroiddev.utils

sealed class Resource<T>(val data: T? =null, val message: String? = null) {
    class Success<T>(data: T?,message: String? = null): Resource<T>(data, message)
    class Failure<T>(message: String?,data: T? = null ): Resource<T>(data, message)
}