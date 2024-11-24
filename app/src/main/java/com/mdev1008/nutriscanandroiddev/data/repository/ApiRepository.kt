package com.mdev1008.nutriscanandroiddev.data.repository

import com.mdev1008.nutriscanandroiddev.data.model.Product
import com.google.gson.GsonBuilder
import com.mdev1008.nutriscanandroiddev.data.ApiRequests
import com.mdev1008.nutriscanandroiddev.data.model.Allergen
import com.mdev1008.nutriscanandroiddev.data.model.DietaryRestriction
import com.mdev1008.nutriscanandroiddev.data.model.RecommendedProductsResponseDto
import com.mdev1008.nutriscanandroiddev.utils.AppResources
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import com.mdev1008.nutriscanandroiddev.utils.AppResources.Companion.getProductDetailsFields
import com.mdev1008.nutriscanandroiddev.utils.Resource
import java.net.UnknownHostException
import com.mdev1008.nutriscanandroiddev.utils.AppResources.Companion.UNKNOWN_HOST_EXCEPTION_MESSAGE
import com.mdev1008.nutriscanandroiddev.utils.AppResources.Companion.UNKNOWN_ERROR_MESSAGE
import com.mdev1008.nutriscanandroiddev.utils.AppResources.Companion.PRODUCT_NOT_FOUND_MESSAGE
import com.mdev1008.nutriscanandroiddev.utils.AppResources.Companion.CONNECTION_TIMEOUT_MESSAGE
import com.mdev1008.nutriscanandroiddev.utils.debugLogger
import com.mdev1008.nutriscanandroiddev.utils.errorLogger
import okhttp3.OkHttpClient
import java.lang.IndexOutOfBoundsException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class ApiRepository {
    private val gson = GsonBuilder().setLenient().create()
    private val connectTimeout : Long = 20
    private val readTimeout : Long = 20
    private val writeTimeout : Long = 20

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(connectTimeout, TimeUnit.SECONDS)
        .readTimeout(readTimeout, TimeUnit.SECONDS)
        .writeTimeout(writeTimeout, TimeUnit.SECONDS)
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(AppResources.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    private val apiRequests = retrofit.create<ApiRequests>()

    fun getProductDetails(productId: String): Resource<Product>{
        debugLogger("fetching details for product $productId")
        try {
            val response = apiRequests.getProductDetailsById(productId, getProductDetailsFields()).execute()
            debugLogger(response.body().toString())
            if (response.isSuccessful){
                val productDetails = response.body()?.products?.get(0)
                debugLogger("response success in app repository")
                return Resource.Success(data = productDetails)
            }else{
                errorLogger("response failure in app repository")
                return Resource.Failure(message = response.message().toString())
            }
        }catch (e: Exception){
            errorLogger(e.toString())
            return when(e){
                is UnknownHostException -> Resource.Failure(message = UNKNOWN_HOST_EXCEPTION_MESSAGE)
                is IndexOutOfBoundsException -> Resource.Failure(message = PRODUCT_NOT_FOUND_MESSAGE)
                is SocketTimeoutException -> Resource.Failure(message = CONNECTION_TIMEOUT_MESSAGE)
                else -> Resource.Failure(message = UNKNOWN_ERROR_MESSAGE)
            }
        }
    }

    fun getRecommendedProducts(
        category: List<String>,
        dietaryRestrictions: List<DietaryRestriction>,
        allergens: List<Allergen>
    ): RecommendedProductsResponseDto?{
        val categories = category[0]
        val response = apiRequests.getRecommendedProducts(
            fields = AppResources.getRecommendedProductFields(),
            categories = categories,
            sortBy = "",
            allergenTags = allergens.joinToString(",-") { it.allergenStrings.joinToString(",-") },
            ingredientAnalysisTags = dietaryRestrictions.joinToString(",") { it.response }
        ).execute()
        return response.body()

    }
}