package com.mdev1008.nutriscanandroiddev

import android.app.Application
import com.mdev1008.nutriscanandroiddev.data.AppDatabase
import com.mdev1008.nutriscanandroiddev.data.repository.ApiRepository
import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository

class NutriScanApplication: Application() {
    private lateinit var database: AppDatabase
    lateinit var apiRepository: ApiRepository
    lateinit var dbRepository: DbRepository

   override fun onCreate() {
        super.onCreate()
       database = AppDatabase.getInstance(this)
       apiRepository = ApiRepository()
       dbRepository = DbRepository(database)

   }
}