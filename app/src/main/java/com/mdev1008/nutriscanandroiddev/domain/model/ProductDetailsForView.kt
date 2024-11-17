package com.mdev1008.nutriscanandroiddev.domain.model

import com.mdev1008.nutriscanandroiddev.data.model.Product
import com.mdev1008.nutriscanandroiddev.data.model.getMainDetailsForView
import com.mdev1008.nutriscanandroiddev.data.model.getNutrientsForView

data class ProductDetailsForView(
    val mainDetailsForView: MainDetailsForView,
    val nutrients: List<NutrientForView>
)

fun Product.toProductDetailsForView(): ProductDetailsForView{
    return ProductDetailsForView(
        mainDetailsForView = this.getMainDetailsForView(),
        nutrients = this.getNutrientsForView()
    )
}
