package com.mdev1008.nutriscanandroiddev.models.remote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.util.TableInfo

@Entity( tableName = "user_allergen",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"]
        )
    ]
)
data class UserAllergen(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int? = null,
    @ColumnInfo(name = "user_id")val userId: Int,
    @ColumnInfo(name = "allergen") val allergen: Allergen
)


fun List<UserAllergen>.containsAllergen(allergen: Allergen): Boolean{
    return this.filter { userAllergen ->
        userAllergen.allergen == allergen
    }.getOrNull(0) != null
}

fun List<UserAllergen>.removeAllergen(allergen: Allergen): List<UserAllergen>{
    return this.filter {
        it.allergen != allergen
    }
}



fun MutableList<UserAllergen>.getConclusion(productAllergens: List<Allergen>?): String{
    if (this.isEmpty()) return ""
    else if (productAllergens.isNullOrEmpty()) return ""
    val commonAllergens =  productAllergens.filter{ productAllergen ->
        this.containsAllergen(productAllergen)
    }
    return if (commonAllergens.isEmpty())  "No Allergens from your selection"
    else "${commonAllergens.size} allergen(s) of your selection"
}