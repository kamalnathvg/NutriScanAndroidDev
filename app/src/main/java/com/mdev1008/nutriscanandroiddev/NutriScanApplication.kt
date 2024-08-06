package com.mdev1008.nutriscanandroiddev

import android.app.Application
import com.mdev1008.nutriscanandroiddev.models.remote.AppDatabase
import com.mdev1008.nutriscanandroiddev.repositories.ApiRepository
import com.mdev1008.nutriscanandroiddev.repositories.DbRepository

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