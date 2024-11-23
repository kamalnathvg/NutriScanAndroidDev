package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.errorLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RegisterUseCase(
    private val dbRepository: DbRepository
) {
    operator fun invoke(userName: String, password: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        val result = dbRepository.createUserWithUserNamePassword(userName, password)
        if (result.data == null) {
            emit(Resource.Failure(result.message))
            return@flow
        }
        emit(Resource.Success(Unit))
    }
        .flowOn(Dispatchers.IO)
        .catch { e ->
            errorLogger(e.message.toString())
            emit(Resource.Failure(e.message))
        }
}