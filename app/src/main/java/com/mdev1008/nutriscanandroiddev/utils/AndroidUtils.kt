package com.mdev1008.nutriscanandroiddev.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlin.concurrent.timerTask

object BarcodeScanner{

    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_UPC_A
        )
        .enableAutoZoom()
        .build()

    fun startScan(context: Context, callback: (Resource<String>) -> Unit){
        val scanner = GmsBarcodeScanning.getClient(context, options)
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                Log.d("logger", "product scan successfully")
                callback( Resource.Success(barcode.rawValue))
            }
            .addOnCanceledListener {
                Log.d("logger", "action cancelled by user")
                callback( Resource.Failure("action cancelled by user"))
            }
            .addOnFailureListener {
                Log.d("logger", it.message.toString())
                callback( Resource.Failure(it.message.toString()))
            }
    }
}

fun logger(message: String){
    Log.d(AppResources.LOGGER_TAG, message)
}