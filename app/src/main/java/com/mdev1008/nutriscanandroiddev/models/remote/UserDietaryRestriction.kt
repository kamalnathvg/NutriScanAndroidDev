package com.mdev1008.nutriscanandroiddev.models.remote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "user_dietary_restriction",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            childColumns = ["user_id"],
            parentColumns = ["user_id"]
        )
    ]
)
data class UserDietaryRestriction(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("restriction_id") val id: Int? = null,
    @ColumnInfo("user_id") val userId: Int,
    @ColumnInfo("restriction") val dietaryRestriction: DietaryRestriction
)


fun MutableList<UserDietaryRestriction>.containsRestriction(dietaryRestriction: DietaryRestriction): Boolean{
    return this.filter{userDietaryRestriction ->
        userDietaryRestriction.dietaryRestriction == dietaryRestriction
    }.getOrNull(0) != null
}


fun MutableList<UserDietaryRestriction>.removeRestriction(dietaryRestriction: DietaryRestriction): List<UserDietaryRestriction>{
    return this.filter {
        it.dietaryRestriction != dietaryRestriction
    }
}

fun MutableList<UserDietaryRestriction>.getConclusion(productRestrictions: List<DietaryRestriction>?): String{
    if (productRestrictions.isNullOrEmpty()) return "Not data found"
    else if(this.isEmpty()) return ""

    val commonRestrictions = mutableListOf<DietaryRestriction>()
    productRestrictions.forEach { restriction ->
        if (this.containsRestriction(restriction)){
            commonRestrictions.add(restriction)
        }
    }
    return commonRestrictions.joinToString(", "){
        it.conclusionString
    }
}