package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.model.User
import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import com.mdev1008.nutriscanandroiddev.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class GetUserDetailsUseCase(
    private val dbRepository: DbRepository
) {
    operator fun invoke(): Flow<Resource<UserProfileDetails>> = flow {
        withContext(Dispatchers.IO){
            try {
                val userDetails = dbRepository.getUserProfileDetails()
                emit(userDetails)
            }catch (e: Exception){
                emit(Resource.Failure(e.message))
            }
        }
    }
}