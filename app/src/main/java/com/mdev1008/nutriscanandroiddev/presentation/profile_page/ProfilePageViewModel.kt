package com.mdev1008.nutriscanandroiddev.presentation.profile_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.mdev1008.nutriscanandroiddev.NutriScanApplication
import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import com.mdev1008.nutriscanandroiddev.domain.usecase.GetUserDetailsUseCase
import com.mdev1008.nutriscanandroiddev.domain.usecase.SkipUserProfileSetupUseCase
import com.mdev1008.nutriscanandroiddev.domain.usecase.UpdateUserDetailsUseCase
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class ProfilePageState(
    val userProfileDetails: UserProfileDetails? = null,
    val userDetailsFetchState: Status = Status.IDLE,
    val updateUserDetailsState: Status = Status.IDLE,
    val errorMessage: String? = null,
)


class ProfilePageViewModel(
    private val updateUserDetailsUseCase: UpdateUserDetailsUseCase,
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
    private val skipUserProfileSetupUseCase: SkipUserProfileSetupUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfilePageState())
    val uiState = _uiState.asStateFlow()


    fun onEvent(event: ProfilePageEvent){
        when(event){
            is ProfilePageEvent.GetUserDetails -> getUserDetails()
            is ProfilePageEvent.UpdateUserDetails -> updateUserDetails(event.userProfileDetails)
            is ProfilePageEvent.SkipProfileSetup -> skipProfileSetup()
        }
    }

    private fun skipProfileSetup() {
        skipUserProfileSetupUseCase()
    }

    private fun updateUserDetails(userProfileDetails: UserProfileDetails) {
        updateUserDetailsUseCase(userProfileDetails).onEach { result ->
            when(result){
                is Resource.Failure -> {
                    _uiState.update {
                        it.copy(
                            updateUserDetailsState = Status.FAILURE,
                            errorMessage = result.message
                        )
                    }
                    _uiState.update {
                        it.copy(
                            updateUserDetailsState = Status.IDLE
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            updateUserDetailsState = Status.LOADING
                        )
                    }
                }
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            updateUserDetailsState = Status.SUCCESS,
                        )
                    }
                    _uiState.update {
                        it.copy(
                            updateUserDetailsState = Status.IDLE
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getUserDetails() {
        getUserDetailsUseCase().onEach { result ->
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
                            updateUserDetailsState = Status.LOADING
                        )
                    }
                }
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            updateUserDetailsState = Status.SUCCESS,
                            userProfileDetails = result.data
                        )
                    }
                    _uiState.update {
                        it.copy(
                            updateUserDetailsState = Status.IDLE
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    companion object{
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                if (modelClass.isAssignableFrom(ProfilePageViewModel::class.java)){
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    val dbRepository = (application as NutriScanApplication).dbRepository
                    return ProfilePageViewModel(
                        getUserDetailsUseCase = GetUserDetailsUseCase(dbRepository),
                        updateUserDetailsUseCase = UpdateUserDetailsUseCase(dbRepository),
                        skipUserProfileSetupUseCase = SkipUserProfileSetupUseCase(dbRepository)
                    ) as T
                }
                throw IllegalArgumentException("Invalid ViewModel Class")
            }
        }
    }
}