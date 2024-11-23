package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository

class SkipUserProfileSetupUseCase(
    private val dbRepository: DbRepository
){
    operator fun invoke(){
        return dbRepository.skipUserProfileSetup()
    }
}