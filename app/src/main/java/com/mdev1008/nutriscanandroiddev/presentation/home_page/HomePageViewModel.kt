package com.mdev1008.nutriscanandroiddev.presentation.home_page

import com.mdev1008.nutriscanandroiddev.data.model.Product
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mdev1008.nutriscanandroiddev.data.model.SearchHistoryItem
import com.mdev1008.nutriscanandroiddev.data.model.UserPreferenceConclusion
import com.mdev1008.nutriscanandroiddev.data.model.User
import com.mdev1008.nutriscanandroiddev.data.model.UserAllergen
import com.mdev1008.nutriscanandroiddev.data.model.UserDietaryPreference
import com.mdev1008.nutriscanandroiddev.data.model.UserDietaryRestriction
import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.data.model.getConclusion
import com.mdev1008.nutriscanandroiddev.data.model.toAllergens
import com.mdev1008.nutriscanandroiddev.data.repository.ApiRepository
import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.data.model.getNutrientsForView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.mdev1008.nutriscanandroiddev.data.model.toDietaryRestrictions
import com.mdev1008.nutriscanandroiddev.data.model.toSearchHistoryItem
import com.mdev1008.nutriscanandroiddev.domain.model.RecommendedProductForView
import com.mdev1008.nutriscanandroiddev.domain.model.SearchHistoryItemForView
import com.mdev1008.nutriscanandroiddev.domain.usecase.GetRecommendedProductsUseCase
import com.mdev1008.nutriscanandroiddev.domain.usecase.GetSearchHistoryUseCase
import com.mdev1008.nutriscanandroiddev.domain.usecase.GetUserDetailsUseCase
import com.mdev1008.nutriscanandroiddev.utils.Status
import com.mdev1008.nutriscanandroiddev.utils.infoLogger
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime

data class HomePageState(
    val userDetailsFetchState: Status = Status.IDLE,
    val searchHistoryFetchState: Status = Status.IDLE,
    val recommendedProductsFetchSate: Status = Status.IDLE,
    val userProfileDetails: UserProfileDetails? = null,
    val searchHistory: List<SearchHistoryItemForView> = emptyList(),
    val recommendedProducts: List<RecommendedProductForView> = emptyList(),
    val errorMessage: String? = null
)

class HomePageViewModel(
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val getRecommendedProductsUseCase: GetRecommendedProductsUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(HomePageState())
    val uiState = _uiState.asStateFlow()

    init {
        getUserDetails()
        getRecommendedProducts() //TODO: set default product category
    }

    fun emit(event: HomePageEvent){
        when(event){
            is HomePageEvent.GetSearchHistory -> getSearchHistory()
            is HomePageEvent.GetUserDetails -> getUserDetails()
        }
    }

    private fun getSearchHistory() {
        getSearchHistoryUseCase().onEach { result ->
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
                            searchHistoryFetchState = Status.IDLE
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
                    result.data?.let {
                        _uiState.update {
                            it.copy(
                                searchHistoryFetchState = Status.SUCCESS,
                                searchHistory = result.data
                            )
                        }
                        _uiState.update {
                            it.copy(
                                searchHistoryFetchState = Status.IDLE
                            )
                        }
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getRecommendedProducts(category: List<String> = emptyList()) {
        getRecommendedProductsUseCase(categories = category).onEach {result ->
            when(result){
                is Resource.Failure -> {
                    _uiState.update {
                        it.copy(
                            recommendedProductsFetchSate = Status.FAILURE,
                            errorMessage = result.message
                        )
                    }
                    _uiState.update {
                        it.copy(
                            recommendedProductsFetchSate = Status.IDLE
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            recommendedProductsFetchSate = Status.LOADING
                        )
                    }
                }
                is Resource.Success -> {
                    result.data?.let { recommendedProducts ->
                        _uiState.update {
                            it.copy(
                                recommendedProductsFetchSate = Status.SUCCESS,
                                recommendedProducts = recommendedProducts
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getUserDetails() {
        getUserDetailsUseCase().onEach {result ->
            when(result){
                is Resource.Failure -> {
                    _uiState.update {
                        it.copy(
                            userDetailsFetchState = Status.FAILURE,
                            errorMessage = result.message
                        )
                    }
                    _uiState.update {
                        it.copy(
                            userDetailsFetchState = Status.IDLE
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            userDetailsFetchState = Status.LOADING
                        )
                    }
                }
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            userDetailsFetchState = Status.SUCCESS,
                            userProfileDetails = result.data
                        )
                    }
                    _uiState.update {
                        it.copy(userDetailsFetchState = Status.IDLE)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}


class HomePageViewModelFactory(private val apiRepository: ApiRepository, private val dbRepository: DbRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomePageViewModel::class.java)){
            return HomePageViewModel(
                getSearchHistoryUseCase = GetSearchHistoryUseCase(dbRepository),
                getUserDetailsUseCase = GetUserDetailsUseCase(dbRepository),
                getRecommendedProductsUseCase = GetRecommendedProductsUseCase(apiRepository, dbRepository)
            ) as T
        }
        throw  IllegalArgumentException("Invalid ViewModel Class")
    }
}