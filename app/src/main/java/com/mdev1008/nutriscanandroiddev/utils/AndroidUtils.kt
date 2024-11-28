package com.mdev1008.nutriscanandroiddev.utils

import android.util.Log


fun debugLogger(message: String){
    Log.d(AppResources.LOGGER_TAG, message)
}

fun infoLogger(message: String){
    Log.i(AppResources.LOGGER_TAG,message)
}

fun errorLogger(message: String){
    Log.e(AppResources.LOGGER_TAG, message)
}