package com.mdev1008.nutriscanandroiddev.presentation.scan_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.mdev1008.nutriscanandroiddev.NutriScanApplication
import com.mdev1008.nutriscanandroiddev.domain.model.ScanItemForView
import com.mdev1008.nutriscanandroiddev.domain.model.SearchHistoryItemForView
import com.mdev1008.nutriscanandroiddev.domain.usecase.GetScanItemDetailsUseCase
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class ScanPageState(
    val scanState: Status = Status.IDLE,
    val scanList: List<ScanItemForView> = emptyList(),
    val errorMessage: String? = null
)

class ScanPageViewModel(
    private val getScanItemDetailsUseCase: GetScanItemDetailsUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(ScanPageState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ScanPageEvent){
        when(event){
            is ScanPageEvent.GetProductDetailsById -> getProductDetailsById(event.productId)
            is ScanPageEvent.ClearScanList -> clearScanList()
        }
    }

    private fun clearScanList() {
        _uiState.update { ScanPageState() }
    }

    private fun getProductDetailsById(productId: String) {
        val currentList = uiState.value.scanList
        if (currentList.any { it.productId == productId }){
            return
        }
        getScanItemDetailsUseCase(productId).onEach { result ->
            when(result){
                is Resource.Failure -> {
                    _uiState.update {
                        it.copy(
                            scanState = Status.FAILURE,
                            errorMessage = result.message
                        )
                    }
                    _uiState.update {
                        it.copy(
                            scanState = Status.IDLE,
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            scanState = Status.LOADING,
                        )
                    }
                }
                is Resource.Success -> {
                    result.data?.let { scanItem ->
                        val updatedList = uiState.value.scanList.toMutableList()
                        updatedList.add(scanItem)
                        _uiState.update {
                            it.copy(
                                scanState = Status.SUCCESS,
                                scanList = updatedList

                            )
                        }
                    }
                    _uiState.update {
                        it.copy(
                            scanState = Status.IDLE,
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
                if (modelClass.isAssignableFrom(ScanPageViewModel::class.java)){
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    val apiRepository = (application as NutriScanApplication).apiRepository
                    val dbRepository = application.dbRepository
                    return ScanPageViewModel(
                        getScanItemDetailsUseCase = GetScanItemDetailsUseCase(apiRepository, dbRepository)
                    ) as T
                }
                throw IllegalArgumentException("Invalid ViewModel class")
            }
        }
    }
}