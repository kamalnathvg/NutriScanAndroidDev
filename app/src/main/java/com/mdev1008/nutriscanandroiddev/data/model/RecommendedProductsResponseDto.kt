package com.mdev1008.nutriscanandroiddev.data.model


import com.google.gson.annotations.SerializedName

data class RecommendedProductsResponseDto(
    @SerializedName("count")
    val count: Int? = 0,
    @SerializedName("page")
    val page: Int? = 0,
    @SerializedName("page_count")
    val pageCount: Int? = 0,
    @SerializedName("page_size")
    val pageSize: Int? = 0,
    @SerializedName("products")
    val products: List<RecommendedProductDto>? = listOf(),
    @SerializedName("skip")
    val skip: Int? = 0
)