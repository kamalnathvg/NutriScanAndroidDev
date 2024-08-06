package com.mdev1008.nutriscanandroiddev.models.remote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mdev1008.nutriscanandroiddev.models.data.NutrientForView
import com.mdev1008.nutriscanandroiddev.models.data.NutrientType
import com.mdev1008.nutriscanandroiddev.models.data.toNutrientPreference

@Entity(
    tableName = "user_dietary_preference",
    foreignKeys = [
     ForeignKey(
         entity = User::class,
         childColumns = ["user_id"],
         parentColumns = ["user_id"])])
data class UserDietaryPreference (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "preference_id") val id: Int? = null,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "nutrient_type") val nutrientType: NutrientType,
    @ColumnInfo(name = "preference") val nutrientPreference: NutrientPreference?
)

enum class NutrientPreference{
    LOW,
    MODERATE,
    HIGH
}


fun generateUserDietaryPreferences(userId: Int): List<UserDietaryPreference>{
    val preferences = mutableListOf<UserDietaryPreference>()
    NutrientType.entries.forEach { nutrientType ->
        UserDietaryPreference(
            userId = userId,
            nutrientType = nutrientType,
            nutrientPreference = null
        )
    }
    return preferences
}


fun MutableList<UserDietaryPreference>.getPreference(nutrientType: NutrientType): NutrientPreference? {
    return this.filter {
        it.nutrientType == nutrientType
    }.getOrNull(0)?.nutrientPreference
}

fun MutableList<UserDietaryPreference>.upsertPreference(userId: Int,nutrientType: NutrientType, nutrientPreference: NutrientPreference?): MutableList<UserDietaryPreference> {
    if (this.getPreference(nutrientType) == null){
        this.add(
            UserDietaryPreference(
            nutrientType = nutrientType,
            nutrientPreference = nutrientPreference,
            userId = userId
        ))
    }else{
        forEachIndexed { index, preference ->
            if (preference.nutrientType == nutrientType){
                this[index] = preference.copy(
                    nutrientPreference = nutrientPreference
                )
            }
        }
    }


    return this
}

fun MutableList<UserDietaryPreference>.getDietaryPreferenceConclusion(productNutrients: List<NutrientForView>): String {
    val filteredUserPreference = this.filter {
        it.nutrientPreference != null
    }
    if (filteredUserPreference.isEmpty()) return ""
    if (productNutrients.isEmpty()) return "Not enough data"
    val commonPreference = mutableListOf<NutrientForView>()
    productNutrients.forEach { productNutrient ->
        val userPreference = this.getPreference(productNutrient.nutrientType)
        userPreference?.let {

            val productPreference = productNutrient.pointsLevel.toNutrientPreference()
            if (userPreference == productPreference) {
                commonPreference.add(productNutrient)
            }
        }
    }
    return if (commonPreference.isEmpty()) "no nutrients match your preference"
    else "Some nutrients match your preference"

}