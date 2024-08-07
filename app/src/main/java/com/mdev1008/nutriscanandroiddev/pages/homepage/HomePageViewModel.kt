package com.mdev1008.nutriscanandroiddev.pages.homepage

import Product
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mdev1008.nutriscanandroiddev.models.data.SearchHistoryItem
import com.mdev1008.nutriscanandroiddev.models.data.UserPreferenceConclusion.UserPreferenceConclusion
import com.mdev1008.nutriscanandroiddev.models.remote.User
import com.mdev1008.nutriscanandroiddev.models.remote.UserAllergen
import com.mdev1008.nutriscanandroiddev.models.remote.UserDietaryPreference
import com.mdev1008.nutriscanandroiddev.models.remote.UserDietaryRestriction
import com.mdev1008.nutriscanandroiddev.models.remote.getConclusion
import com.mdev1008.nutriscanandroiddev.models.remote.toAllergens
import com.mdev1008.nutriscanandroiddev.repositories.ApiRepository
import com.mdev1008.nutriscanandroiddev.repositories.DbRepository
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.logger
import getNutrientsForView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import toDietaryRestrictions
import toSearchHistoryItem
import java.time.LocalDateTime

data class HomePageState(
    val message: String? = null,
    val searchHistory: List<SearchHistoryItem> = mutableListOf(),
    val productDetailsFetchState: ProductDetailsFetchState = ProductDetailsFetchState.NOT_STARTED,
    val product: Product? = null,
    val user: User? = null,
    val dietaryPreferences: List<UserDietaryPreference> = mutableListOf(),
    val dietaryRestriction: List<UserDietaryRestriction> = mutableListOf(),
    val allergens: List<UserAllergen> = mutableListOf(),
    val userDetailsFetchState: UserDetailsFetchState = UserDetailsFetchState.NOT_STARTED,
    val userPreferencesConclusion: UserPreferenceConclusion = UserPreferenceConclusion()
)

/**
 * keeps track of the product details fetch state when scanning a product barcode
 */
enum class ProductDetailsFetchState{
    LOADING,
    SUCCESS,
    FAILURE,
    NOT_STARTED
}
/**
 * keeps track of the init user details fetch state
 */
enum class UserDetailsFetchState{
    LOADING,
    SUCCESS,
    FAILURE,
    NOT_STARTED
}

class HomePageViewModel(
    private val apiRepository: ApiRepository,
    private val dbRepository: DbRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(HomePageState())
    val uiState = _uiState.asStateFlow()

    init {
     getUserDetails()

    }


    fun emit(event: HomePageEvent){
        when(event){
            is HomePageEvent.FetchSearchHistory -> {
                //TODO: fetch search history from database
                val result: Resource<List<SearchHistoryItem>> = dbRepository.getSearchHistory()
                when(result){
                    is Resource.Failure -> {
                        _uiState.update {
                            it.copy(
                                message = result.message
                            )
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let { searchHistoryItems ->
                            _uiState.update { state ->
                               state.copy(
                                   searchHistory = searchHistoryItems
                               )
                            }
                        }
                    }
                }
            }

            is HomePageEvent.FetchProductDetails -> {
                _uiState.update {
                    it.copy(
                        productDetailsFetchState = ProductDetailsFetchState.LOADING
                    )
                }

                viewModelScope.launch(Dispatchers.IO) {
                    val result: Resource<Product> = apiRepository.getProductDetails(event.productId)
                    when(result) {
                        is Resource.Success -> {
                            logger("product details fetch success in viewModel")
                            upsertItemToSearchHistory(result.data)
                            compareUserPreference(result.data)
                            withContext(Dispatchers.Main){
                                _uiState.update {
                                    it.copy(
                                        product = result.data,
                                        productDetailsFetchState = ProductDetailsFetchState.SUCCESS
                                    )
                                }
                                _uiState.update {
                                    it.copy(
                                        productDetailsFetchState = ProductDetailsFetchState.NOT_STARTED
                                    )
                                }
                            }

                        }

                        is Resource.Failure -> {
                            logger("product details fetch failure in viewModel")
                            withContext(Dispatchers.Main){
                                _uiState.update {
                                    it.copy(
                                        message = result.message,
                                        productDetailsFetchState = ProductDetailsFetchState.FAILURE
                                    )
                                }
                                _uiState.update {
                                    it.copy(
                                        productDetailsFetchState = ProductDetailsFetchState.NOT_STARTED
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HomePageEvent.SignOut -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO){
                        dbRepository.signOut()
                    }
                }
                _uiState.update {
                    HomePageState()
                }

            }

            is HomePageEvent.UpdateUserDetails ->{
                viewModelScope.launch(Dispatchers.IO) {
                    dbRepository.upsertUserProfileDetails(event.userDetails)
                    delay(500)
                }
                event.userDetails.apply {
                    _uiState.update {
                        it.copy(
                            user = this.userDetails,
                            dietaryPreferences = this.userPreferences,
                            dietaryRestriction = this.userRestrictions,
                            allergens = this.userAllergen
                        )
                    }
                }
                getUserDetails()

            }
            HomePageEvent.GetUserDetails -> getUserDetails()
            HomePageEvent.SkipProfilePage -> skipUserProfileSetup()
        }
    }

    private fun skipUserProfileSetup() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                dbRepository.skipUserProfileSetup()
            }
        }catch (e: Exception){
        }


    }

    private fun compareUserPreference(product: Product?) {
        if (product == null) return
        val state = uiState.value
        val dietaryPreferenceConclusion = state.dietaryPreferences
            .toMutableList()
            .getConclusion(product.getNutrientsForView())
        val dietaryRestrictionConclusion = state.dietaryRestriction
            .toMutableList()
            .getConclusion(product.dietaryRestrictions?.toDietaryRestrictions())
        val allergenConclusion = state.allergens
            .toMutableList()
            .getConclusion(product.allergensHierarchy?.toAllergens())
            val userPreferencesConclusion = UserPreferenceConclusion(
                userDietaryPreferenceConclusion = dietaryPreferenceConclusion,
                userDietaryRestrictionConclusion = dietaryRestrictionConclusion,
                userAllergenConclusion = allergenConclusion
            )
        _uiState.update {
            it.copy(
                userPreferencesConclusion = userPreferencesConclusion
            )
        }
    }

    private fun checkIfItemInSearchHistory(productId: String): SearchHistoryItem? {
        uiState.value.searchHistory.forEach { item ->
            if (item.productId == productId) return item
        }
        return null
    }

    private suspend fun upsertItemToSearchHistory(product: Product?) {

        if (product == null) return
        val itemInSearchHistory = checkIfItemInSearchHistory(product.productId)
        logger("item in search history :${itemInSearchHistory != null}")

        var itemToAdd: SearchHistoryItem? = itemInSearchHistory?.copy(
            timeStamp = LocalDateTime.now()
        )
        if (itemInSearchHistory == null){
            uiState.value.user?.id?.let {userId ->
                itemToAdd = product.toSearchHistoryItem(userId)
            }
        }
        withContext(Dispatchers.IO){
            logger("adding item with itemId: ${itemToAdd?.id}")
            itemToAdd?.let {
                dbRepository.addItemToSearchHistory(it)
            }
            delay(500)
        }
        updateSearchHistory()
    }
    private suspend fun updateSearchHistory(){
        viewModelScope.launch {
            var updatedSearchHistory: List<SearchHistoryItem>
            withContext(Dispatchers.IO){
                updatedSearchHistory = dbRepository.getSearchHistory().data ?: mutableListOf()
            }
            _uiState.update {
                it.copy(
                    searchHistory = updatedSearchHistory
                )
            }
        }
    }
    private fun getUserDetails() {
        _uiState.update {
            it.copy(userDetailsFetchState = UserDetailsFetchState.LOADING)
        }
        viewModelScope.launch(Dispatchers.IO) {

            val searchHistory = dbRepository.getSearchHistory().data ?: mutableListOf()
            val profileDetails = dbRepository.getUserProfileDetails()
            if (profileDetails is Resource.Success){
                withContext(Dispatchers.Main){
                    profileDetails.data?.let {details ->
                        logger(profileDetails.data.userDetails.userName)
                        _uiState.update {
                            it.copy(
                                user = details.userDetails,
                                searchHistory = searchHistory,
                                dietaryPreferences = details.userPreferences,
                                dietaryRestriction = details.userRestrictions,
                                allergens = details.userAllergen,
                                userDetailsFetchState = UserDetailsFetchState.SUCCESS
                            )
                        }
                    }
                }
            }

            logger((profileDetails.data?.userDetails ?: "null").toString())
            withContext(Dispatchers.Main){
                _uiState.update {
                    it.copy(userDetailsFetchState = UserDetailsFetchState.NOT_STARTED)
                }
            }
        }
    }


}


class HomePageViewModelFactory(private val apiRepository: ApiRepository,private val dbRepository: DbRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomePageViewModel::class.java)){
            return HomePageViewModel(
                apiRepository = apiRepository,
                dbRepository = dbRepository
            ) as T
        }
        throw  IllegalArgumentException("Invalid ViewModel Class")
    }
}