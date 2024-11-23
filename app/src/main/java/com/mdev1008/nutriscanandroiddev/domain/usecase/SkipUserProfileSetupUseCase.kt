package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SkipUserProfileSetupUseCase(
    private val dbRepository: DbRepository
){
    suspend operator fun invoke() {
        return withContext(Dispatchers.IO) {
            dbRepository.skipUserProfileSetup()
        }
    }
}