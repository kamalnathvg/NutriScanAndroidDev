
import com.google.gson.annotations.SerializedName
import com.mdev1008.nutriscanandroiddev.models.data.MainDetailsForView
import com.mdev1008.nutriscanandroiddev.models.data.NutrientForView
import com.mdev1008.nutriscanandroiddev.models.data.NutrientType
import com.mdev1008.nutriscanandroiddev.models.data.SearchHistoryItem
import com.mdev1008.nutriscanandroiddev.models.data.getContentPerHundredGram
import com.mdev1008.nutriscanandroiddev.models.data.getDescription
import com.mdev1008.nutriscanandroiddev.models.data.getHealthCategory
import com.mdev1008.nutriscanandroiddev.models.data.getNutrientCategory
import com.mdev1008.nutriscanandroiddev.models.data.getPointsLevel
import com.mdev1008.nutriscanandroiddev.models.data.getServingUnit
import com.mdev1008.nutriscanandroiddev.models.remote.DietaryRestriction
import com.mdev1008.nutriscanandroiddev.models.remote.NutriScoreData
import com.mdev1008.nutriscanandroiddev.models.remote.toDietaryRestriction
import com.mdev1008.nutriscanandroiddev.utils.getImageUrl
import java.time.LocalDateTime

data class Product(
    @SerializedName("code")
    var productId : String,

    @SerializedName("product_name")
    val productName : String,

    @SerializedName("image_url")
    val imageUrl : String?,

    @SerializedName("nutriments")
    val nutrients: Nutrients?,

    @SerializedName("ingredients")
    val ingredients: List<Ingredients>?,

    @SerializedName("brands")
    val brand : String?,

    @SerializedName("nutriscore_grade")
    val nutriScoreGrade : String?,

    @SerializedName("nutriscore_data")
    val nutriScoreData: NutriScoreData?,

    @SerializedName("categories_hierarchy")
    val categoriesHierarchy: List<String>?,

    @SerializedName("allergens_hierarchy")
    val allergensHierarchy: List<String>?,

    @SerializedName("ingredients_analysis_tags")
    val dietaryRestrictions: List<String>?,
)



fun Product.getMainDetailsForView(): MainDetailsForView{
    val productId = this.productId
    val imageUrl = this.imageUrl ?: this.productId.getImageUrl()
    val productName = this.productName
    val productBrand = this.brand
    val healthCategory = this.nutriScoreGrade.getHealthCategory()

    return  MainDetailsForView(
        productId = productId,
        imageUrl = imageUrl,
        productName = productName,
        productBrand = productBrand,
        healthCategory = healthCategory
    )
}
fun Product.getNutrientsForView(): List<NutrientForView>{
    val nutrients: MutableList<NutrientForView> = mutableListOf()

    NutrientType.values().forEach { nutrientType ->
        getNutrientForViewByType(nutrientType, this)?.let { nutrients.add(it) }
    }
    return nutrients

}

private fun getNutrientForViewByType(nutrientType: NutrientType, product: Product): NutrientForView? {
    if (product.nutriScoreData == null || product.nutrients == null){
        return null
    }
    val contentPerHundredGram = nutrientType.getContentPerHundredGram(product.nutrients)
    val pointsLevel = nutrientType.getPointsLevel(product.nutriScoreData)
    val healthCategory = pointsLevel.getHealthCategory(nutrientType)
    val description = nutrientType.getDescription(pointsLevel)
    val servingUnit = nutrientType.getServingUnit()
    val nutrientCategory = nutrientType.getNutrientCategory(pointsLevel)
    return NutrientForView(
        nutrientType = nutrientType,
        contentPerHundredGrams = contentPerHundredGram,
        description = description,
        pointsLevel = pointsLevel,
        healthCategory = healthCategory,
        servingUnit = servingUnit,
        nutrientCategory = nutrientCategory
    )
}

fun Product.toSearchHistoryItem(userId: Int): SearchHistoryItem{
    val productId = this.productId
    val imageUrl = this.imageUrl ?: this.productId.getImageUrl() ?: ""
    val productName = this.productName
    val productBrand = this.brand ?: ""
    val healthCategory = this.nutriScoreGrade.getHealthCategory()

    return  SearchHistoryItem(
        userId= userId,
        productId = productId,
        imageUrl = imageUrl,
        productName = productName,
        productBrand = productBrand,
        healthCategory = healthCategory,
        timeStamp = LocalDateTime.now()
    )
}


fun List<String>.toDietaryRestrictions(): List<DietaryRestriction>{
    val restrictions = mutableListOf<DietaryRestriction>()
    this.forEach { restrictionString ->
        restrictionString.toDietaryRestriction()?.let {
            restrictions.add(it)
        }
    }
    return restrictions
}