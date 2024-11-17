package com.mdev1008.nutriscanandroiddev.domain.usecase

import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import com.mdev1008.nutriscanandroiddev.domain.model.SearchHistoryItemForView
import com.mdev1008.nutriscanandroiddev.domain.model.toSearchHistoryForView
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.errorLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetSearchHistoryUseCase(
    private val dbRepository: DbRepository
){
    operator fun invoke(): Flow<Resource<List<SearchHistoryItemForView>>> = flow{
        try {
            val searchHistory = dbRepository.getSearchHistory().data
            if (searchHistory == null) {
                emit(Resource.Failure("Unable to find"))
                return@flow
            }
            val searchHistoryItemForView = searchHistory.map { it.toSearchHistoryForView() }
            emit(Resource.Success(searchHistoryItemForView))
        }catch (e: Exception){
            errorLogger(e.message.toString())
            emit(Resource.Failure(e.message))
        }
    }
}