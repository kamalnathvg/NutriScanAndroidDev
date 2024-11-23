package com.mdev1008.nutriscanandroiddev.presentation.login_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mdev1008.nutriscanandroiddev.data.repository.DbRepository
import com.mdev1008.nutriscanandroiddev.domain.usecase.LoginUseCase
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class LoginPageState(
    val loginState: Status = Status.IDLE,
    val errorMessage: String? = null
)

class LoginViewModel(
    private val loginUseCase: LoginUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(LoginPageState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: LoginPageEvent){
        when(event){
            is LoginPageEvent.LoginWithUserNamePassword -> loginWithUserNamePassword(event.email, event.password)
        }
    }

    private fun loginWithUserNamePassword(email: String, password: String) {
        loginUseCase(email, password).onEach { result ->
            when(result){
                is Resource.Failure -> {
                    _uiState.update {
                        it.copy(
                            loginState = Status.FAILURE,
                            errorMessage = result.message
                        )
                    }
                    _uiState.update {
                        it.copy(
                            loginState = Status.IDLE
                        )
                    }
                }
                is Resource.Loading ->{
                    _uiState.update {
                        it.copy(
                            loginState = Status.LOADING
                        )
                    }
                }
                is Resource.Success ->{
                    _uiState.update {
                        it.copy(
                            loginState = Status.SUCCESS
                        )
                    }
                    _uiState.update {
                        it.copy(
                            loginState = Status.IDLE
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}






class LoginViewModelFactory(
    private val dbRepository: DbRepository,
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(
             loginUseCase = LoginUseCase(dbRepository)
            ) as T
        }
        throw IllegalArgumentException("Invalid ViewModel Class")
    }
}
