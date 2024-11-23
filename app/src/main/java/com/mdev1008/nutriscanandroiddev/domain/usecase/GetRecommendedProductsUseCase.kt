package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.repository.ApiRepository
import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import com.mdev1008.nutriscanandroiddev.domain.model.RecommendedProductForView
import com.mdev1008.nutriscanandroiddev.domain.model.parseForView
import com.mdev1008.nutriscanandroiddev.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

data class GetRecommendedProductsUseCase(
    private val apiRepository: ApiRepository,
    private val dbRepository: DbRepository
){
    operator fun invoke(
       categories: List<String>,
    ): Flow<Resource<List<RecommendedProductForView>>> = flow{
        emit(Resource.Loading())
        val currentUser = dbRepository.getUserProfileDetails().data
        if (currentUser == null) {
            emit(Resource.Failure("Unable to fetch user details"))
            return@flow
        }
        val response = apiRepository.getRecommendedProducts(
            category = categories,
            dietaryRestrictions = currentUser.userRestrictions.map { it.dietaryRestriction },
            allergens = currentUser.userAllergen.map { it.allergen }
        )
        if (response == null) {
            emit(Resource.Failure("Unable to fetch Recommended Products"))
            return@flow
        }
        val recommendedProducts = response.products?.map { it.parseForView() }
        emit(Resource.Success(recommendedProducts))
    }
        .flowOn(Dispatchers.IO)
        .catch { e ->

            emit(Resource.Failure(e.message.toString()))
        }
}
