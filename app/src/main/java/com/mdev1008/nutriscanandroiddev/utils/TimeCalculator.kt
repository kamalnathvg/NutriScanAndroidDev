package com.mdev1008.nutriscanandroiddev.utils

import java.time.Duration

class TimeCalculator {
    companion object{
        fun getTime( duration: Duration) : String{
            return when{
                duration.toDays() > 1 -> "${duration.toDays()} days ago"
                duration.toDays().toInt() == 1 -> "1 day ago"
                duration.toHours() > 1 -> "${duration.toHours()} hours ago"
                duration.toHours().toInt() == 1 -> "1 hour ago"
                duration.toHours() < 1 -> "Just Now"
                else -> ""
            }
        }
    }
}