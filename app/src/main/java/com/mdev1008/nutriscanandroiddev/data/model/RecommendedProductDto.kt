package com.mdev1008.nutriscanandroiddev.data.model


import com.google.gson.annotations.SerializedName

data class RecommendedProductDto(
    @SerializedName("brands")
    val brands: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("nutriscore_grade")
    val nutriscoreGrade: String? = null,
    @SerializedName("product_name")
    val productName: String? = null
)