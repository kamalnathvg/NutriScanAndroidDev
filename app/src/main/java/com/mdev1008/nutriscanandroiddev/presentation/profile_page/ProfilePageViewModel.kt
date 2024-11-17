package com.mdev1008.nutriscanandroiddev.presentation.profile_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.domain.usecase.GetUserDetailsUseCase
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
    private val getUserDetailsUseCase: GetUserDetailsUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(ProfilePageState())
    val uiState = _uiState.asStateFlow()


    fun onEvent(event: ProfilePageEvent){
        when(event){
            is ProfilePageEvent.GetUserDetails -> getUserDetails()
            is ProfilePageEvent.UpdateUserDetails -> updateUserDetails(event.userProfileDetails)
        }
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

}