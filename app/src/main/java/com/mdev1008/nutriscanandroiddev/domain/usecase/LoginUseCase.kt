package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.model.User
import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.errorLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginUseCase(
    private val dbRepository: DbRepository
) {
    operator fun invoke(userName: String, password: String): Flow<Resource<User>> = flow{
        try {
            val result = dbRepository.signInWithUserNamePassword(userName, password)
            if (result.data == null) {
                emit(Resource.Failure(result.message))
                return@flow
            }

            emit(Resource.Success(result.data))
        }catch (e: Exception){
            errorLogger(e.message.toString())
            emit(Resource.Failure(e.message.toString()))
        }
    }
}
