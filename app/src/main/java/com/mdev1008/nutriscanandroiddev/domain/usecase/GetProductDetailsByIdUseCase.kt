package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.repository.ApiRepository
import com.mdev1008.nutriscanandroiddev.domain.model.ProductDetailsForView
import com.mdev1008.nutriscanandroiddev.domain.model.toProductDetailsForView
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.errorLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetProductDetailsByIdUseCase(
    private val apiRepository: ApiRepository
) {
    operator fun invoke(productId: String): Flow<Resource<ProductDetailsForView>> = flow{
        val result = apiRepository.getProductDetails(productId)
        if (result.data == null) {
            emit(Resource.Failure(result.message))
            return@flow
        }
        val productDetailsForView = result.data.toProductDetailsForView()
        emit(Resource.Success(productDetailsForView))
    }
        .flowOn(Dispatchers.IO)
        .catch { e ->
            errorLogger(e.message.toString())
            emit(Resource.Failure(e.message.toString()))
        }
}