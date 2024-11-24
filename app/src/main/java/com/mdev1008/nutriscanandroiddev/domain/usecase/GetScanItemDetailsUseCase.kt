package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.repository.ApiRepository
import com.mdev1008.nutriscanandroiddev.domain.model.ScanItemForView
import com.mdev1008.nutriscanandroiddev.domain.model.toProductDetailsForView
import com.mdev1008.nutriscanandroiddev.domain.model.toScanItemForView
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.errorLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetScanItemDetailsUseCase(
    private val apiRepository: ApiRepository
) {
    operator fun invoke(productId: String): Flow<Resource<ScanItemForView>> = flow {
        emit(Resource.Loading())
        val result = apiRepository.getProductDetails(productId)
        if (result.data == null){
            emit(Resource.Failure(result.message))
            return@flow
        }
        val scanItem = result.data.toScanItemForView()
        emit(Resource.Success(scanItem))
    }
        .flowOn(Dispatchers.IO)
        .catch { e ->
            errorLogger("GetScanItemDetailsUseCase: ${e.message.toString()}")
            emit(Resource.Failure(e.message))
        }
}