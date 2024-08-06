package com.zero1labs.nutriscan.data.models.remote

import Product
import com.google.gson.annotations.SerializedName

data class ProductDetailsResponse(
    @SerializedName("count")
    val itemCount: Int,

    @SerializedName("page")
    val currentPage : Int,

    @SerializedName("page_count")
    val pageCount : Int,

    @SerializedName("products")
    val products : List<Product>
)
