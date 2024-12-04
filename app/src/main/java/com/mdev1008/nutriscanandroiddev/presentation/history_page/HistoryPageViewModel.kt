package com.mdev1008.nutriscanandroiddev.presentation.history_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.mdev1008.nutriscanandroiddev.NutriScanApplication
import com.mdev1008.nutriscanandroiddev.domain.model.SearchHistoryItemForView
import com.mdev1008.nutriscanandroiddev.domain.usecase.GetSearchHistoryUseCase
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class HistoryPageState(
    val searchHistoryFetchState: Status = Status.IDLE,
    val searchHistory: List<SearchHistoryItemForView> = emptyList(),
    val errorMessage: String? = null
)
class HistoryPageViewModel(
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(HistoryPageState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: HistoryPageEvent){
        when(event){
            HistoryPageEvent.GetSearchHistory -> getSearchHistory()
        }
    }

    private fun getSearchHistory() {
        getSearchHistoryUseCase().onEach {result ->
            when(result){
                is Resource.Failure -> {
                    _uiState.update {
                        it.copy(
                            searchHistoryFetchState = Status.FAILURE,
                            errorMessage = result.message
                        )
                    }
                    _uiState.update {
                        it.copy(
                            searchHistoryFetchState = Status.IDLE,
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            searchHistoryFetchState = Status.LOADING
                        )
                    }
                }
                is Resource.Success -> {
                    result.data?.let { searchHistory ->
                        _uiState.update {
                            it.copy(
                                searchHistoryFetchState = Status.SUCCESS,
                                searchHistory = searchHistory
                            )
                        }
                    }
                    _uiState.update {
                        it.copy(
                            searchHistoryFetchState = Status.IDLE
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    companion object{
        val Factory: ViewModelProvider.Factory = object: ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                if (modelClass.isAssignableFrom(HistoryPageViewModel::class.java)){
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    val dbRepository = (application as NutriScanApplication).dbRepository

                    return HistoryPageViewModel(
                        getSearchHistoryUseCase = GetSearchHistoryUseCase(dbRepository)
                    ) as T
                }
                throw IllegalArgumentException("Invalid ViewModel Class")
            }
        }
    }

}