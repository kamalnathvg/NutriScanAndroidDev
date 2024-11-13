package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import com.mdev1008.nutriscanandroiddev.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RegisterUseCase(
    private val dbRepository: DbRepository
) {
    operator fun invoke(userName: String, password: String): Flow<Resource<Unit>> = flow {
        dbRepository.createUserWithUserNamePassword(userName, password)
        //TODO: incomplete
    }
}