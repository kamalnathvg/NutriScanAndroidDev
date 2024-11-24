package com.mdev1008.nutriscanandroiddev.presentation.scan_page

sealed class ScanPageEvent {
    data class GetProductDetailsById(val productId: String): ScanPageEvent()
}