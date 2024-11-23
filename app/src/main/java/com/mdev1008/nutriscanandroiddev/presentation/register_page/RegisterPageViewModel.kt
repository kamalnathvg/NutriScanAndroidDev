package com.mdev1008.nutriscanandroiddev.presentation.register_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.mdev1008.nutriscanandroiddev.NutriScanApplication
import com.mdev1008.nutriscanandroiddev.domain.usecase.RegisterUseCase
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class RegisterPageState(
    val registerState: Status = Status.IDLE,
    val errorMessage: String? = null
)

class RegisterPageViewModel(
    private val registerUseCase: RegisterUseCase
): ViewModel(){
    private val _uiState = MutableStateFlow(RegisterPageState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: RegisterPageEvent){
        when(event){
            is RegisterPageEvent.RegisterWithUserNamePassword -> registerWithUserNamePassword(event.userName, event.password)
        }
    }

    private fun registerWithUserNamePassword(userName: String, password: String) {
        registerUseCase(userName, password).onEach { result ->
            when(result){
                is Resource.Failure -> {
                    _uiState.update {
                        it.copy(
                            registerState = Status.FAILURE,
                            errorMessage = result.message
                        )
                    }
                    _uiState.update {
                        it.copy(
                            registerState = Status.IDLE
                        )
                    }
                }
                is Resource.Loading -> {
                    _uiState.update {
                        it.copy(
                            registerState = Status.LOADING
                        )
                    }
                }
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            registerState = Status.SUCCESS
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
                if (modelClass.isAssignableFrom(RegisterPageViewModel::class.java)){
                    val application = checkNotNull(extras[APPLICATION_KEY])
                    return RegisterPageViewModel(
                        registerUseCase = RegisterUseCase(
                            (application as NutriScanApplication).dbRepository
                        )
                    ) as T
                }
                throw IllegalArgumentException("Invalid ViewModel class")
            }
        }
    }
}