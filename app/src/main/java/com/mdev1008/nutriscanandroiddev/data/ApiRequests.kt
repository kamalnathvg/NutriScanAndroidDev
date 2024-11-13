package com.mdev1008.nutriscanandroiddev.data

import com.mdev1008.nutriscanandroiddev.data.model.ProductDetailsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ApiRequests {

    @Headers("Accept: application/json")
    @GET("search/")
    fun getProductDetailsById(
        @Query("code") productId: String,
        @Query("fields") fields: String,
    ): Call<ProductDetailsResponse>

}