package com.mdev1008.nutriscanandroiddev.utils

import android.util.Log
import androidx.core.text.isDigitsOnly
import java.lang.Character.isLetter
import java.lang.Character.isLowerCase
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.Duration
import java.time.ZoneId


fun String.isValidUserName(): Pair<Boolean,String>{
    if (this.length < 5) return Pair(false , "username too short")
    this.forEach {
        if (!it.isLetter() && !it.isDigit()){
            return Pair(false,"Username should contain letters or numbers only")
        }
    }
    return Pair(true, "valid username")
}



fun String.isValidPassword(): Pair<Boolean, String> {
    if (this.length < 8) return Pair(false,"password too short")
    val symbols = "~`!@#$%^&*()_-+={[}]|:;'\"\\<,>.?/"
    val containsSymbol = this.any {
        symbols.contains(it)
    }
    if (!containsSymbol) return Pair(false, "should contain at least 1 symbol")

    val containsUpperCase = this.any {
        it.isUpperCase()
    }
    if (!containsUpperCase) return Pair(false, "should contain at least 1 upper case letter")

    val containsLowerCase = this.any{
        it.isLowerCase()
    }
    if (!containsLowerCase) return Pair(false, "should contain at least 1 lower case letter")

    val containsDigit = this.any{
        it.isDigit()
    }
    if (!containsDigit) return Pair(false, "should contain at least 1 number")

    return Pair(true, "valid password")
}

fun String.encrypt(): String {
    val bytes = this.toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
    return digest.joinToString(""){"%02x".format(it)}
}

fun LocalDateTime.getDurationTillNow(): Duration{
    return Duration.between(this,LocalDateTime.now())
}

fun Duration.toReadableString(): String{
    return when{
        this.toDays() > 1 -> "${this.toDays()} days ago"
        this.toDays().toInt() == 1 -> "1 day ago"
        this.toHours() > 1 -> "${this.toHours()} hours ago"
        this.toHours().toInt() == 1 -> "1 hour ago"
        this.toHours() < 1 -> "Just Now"
        else -> ""
    }
}

fun String.getImageUrl(): String?{
    val imageBaseUrl = "https://images.openfoodfacts.org/images/products/"
    if (!this.isValidProductId()) return null
//    if (this.length < 13) this.padStart(length = 13 - this.length , padChar = '0')
    val paddedProductId = this.padStart(13 - this.length, '0')
    val imageUrl = buildString {
        append(imageBaseUrl)
        append(paddedProductId.subSequence(0,3)).append('/')
        append(paddedProductId.subSequence(3,6)).append('/')
        append(paddedProductId.subSequence(6,9)).append('/')
        append(paddedProductId.subSequence(9,13)).append("/1.jpg")
    }
    Log.d("logger", imageUrl)
    return imageUrl
}

fun String.isValidProductId(): Boolean {
    val result = this.filterNot {
        isDigitsOnly()
    }
    return result.isEmpty()
}

fun String.greet(): String{
    return "Hi $this"
}