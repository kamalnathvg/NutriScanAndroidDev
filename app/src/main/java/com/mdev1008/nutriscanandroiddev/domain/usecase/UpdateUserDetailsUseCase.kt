package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.model.User
import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.errorLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

class UpdateUserDetailsUseCase(
    private val dbRepository: DbRepository
) {
    operator fun invoke(userProfileDetails: UserProfileDetails): Flow<Resource<Unit>> =flow{
        emit(Resource.Loading())
        try {
            val result = dbRepository.upsertUserProfileDetails(userProfileDetails)
            if (result.data == null){
                emit(Resource.Failure(result.message))
                return@flow
            }
            emit(Resource.Success(Unit))
        }catch (e: Exception){
            errorLogger(e.message.toString())
            emit(Resource.Failure(e.message))
        }
    }
}