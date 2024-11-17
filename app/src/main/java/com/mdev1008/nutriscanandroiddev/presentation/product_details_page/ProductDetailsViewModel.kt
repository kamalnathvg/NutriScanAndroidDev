package com.mdev1008.nutriscanandroiddev.presentation.product_details_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mdev1008.nutriscanandroiddev.data.repository.ApiRepository
import com.mdev1008.nutriscanandroiddev.domain.model.ProductDetailsForView
import com.mdev1008.nutriscanandroiddev.domain.usecase.GetProductDetailsByIdUseCase
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class ProductDetailsPageState(
    val productDetailsFetchState: Status = Status.IDLE,
    val productDetailsForView: ProductDetailsForView? = null,
    val errorMessage: String? = null
)

class ProductDetailsViewModel(
    private val getProductDetailsByIdUseCase: GetProductDetailsByIdUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailsPageState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ProductDetailsPageEvent){
        when(event){
            is ProductDetailsPageEvent.GetProductDetailsById -> getProductDetailsById(event.productId)
        }
    }

    private fun getProductDetailsById(productId: String) {
        getProductDetailsByIdUseCase(productId).onEach { result ->
            when(result){
                is Resource.Failure -> {
                    _uiState.update {
                        it.copy(
                            productDetailsFetchState = Status.FAILURE,
                            errorMessage = result.message
                        )
                    }
                    _uiState.update {
                        it.copy(
                            productDetailsFetchState = Status.IDLE
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update {
                        ProductDetailsPageState(
                            productDetailsFetchState = Status.LOADING
                        )
                    }
                }
                is Resource.Success -> {
                    _uiState.update {
                        ProductDetailsPageState(
                            productDetailsFetchState = Status.SUCCESS,
                            productDetailsForView = result.data
                        )
                    }
                    _uiState.update {
                        it.copy(
                            productDetailsFetchState = Status.IDLE
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}

class ProductDetailsViewModelFactory(private val apiRepository: ApiRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailsViewModel::class.java)){
            val getProductDetailsByIdUseCase = GetProductDetailsByIdUseCase(apiRepository)
            return ProductDetailsViewModel(
                getProductDetailsByIdUseCase = getProductDetailsByIdUseCase
            ) as T
        }
        throw IllegalArgumentException("Invalid ViewModel Class")
    }
}