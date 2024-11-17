package com.mdev1008.nutriscanandroiddev.presentation.product_details_page

sealed class ProductDetailsPageEvent {
    data class GetProductDetailsById(val productId: String): ProductDetailsPageEvent()
}