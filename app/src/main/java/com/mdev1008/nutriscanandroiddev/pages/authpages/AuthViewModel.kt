package com.mdev1008.nutriscanandroiddev.pages.authpages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mdev1008.nutriscanandroiddev.models.remote.User
import com.mdev1008.nutriscanandroiddev.repositories.DbRepository
import com.mdev1008.nutriscanandroiddev.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AuthState(
    val registerState: RegisterState = RegisterState.NOT_STARTED,
    val signInState: SignInState = SignInState.NOT_STARTED,
    val message: String = ""
)

enum class RegisterState{
    LOADING,
    SUCCESS,
    FAILURE,
    NOT_STARTED
}
enum class SignInState{
    LOADING,
    SUCCESS,
    FAILURE,
    NOT_STARTED
}


class AuthViewModel(private val dbRepository: DbRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState = _uiState.asStateFlow()

    fun emit(event: AuthEvent){
        when(event){

            is AuthEvent.RegisterWithUserNamePassword -> {
                changeRegisterState(RegisterState.LOADING)

                viewModelScope.launch(Dispatchers.IO) {
                    val result: Resource<User> = dbRepository.createUserWithUserNamePassword(
                        userName = event.userName,
                        password = event.password
                    )
                    withContext(Dispatchers.Main){
                    when(result){
                        is Resource.Success -> {
                                changeRegisterState(RegisterState.SUCCESS,result.message)
                            }
                        is Resource.Failure -> {
                                changeRegisterState(RegisterState.FAILURE,result.message)
                             }
                        }
                        changeRegisterState(RegisterState.NOT_STARTED)
                    }
                }

            }
            is AuthEvent.SignInWithUserNamePassword -> {
                changeSignInState(SignInState.LOADING)
                viewModelScope.launch(Dispatchers.IO) {
                    val result: Resource<User> = dbRepository.signInWithUserNamePassword(
                        userName = event.userName,
                        password = event.password
                    )
                    withContext(Dispatchers.Main){
                        when(result){
                            is Resource.Success -> {
                                changeSignInState(SignInState.SUCCESS,result.message)
                            }
                            is Resource.Failure -> {
                                changeSignInState(SignInState.FAILURE,result.message)
                            }
                        }
                        changeSignInState(SignInState.NOT_STARTED)
                    }
                }
            }
        }
    }
    private fun changeRegisterState(registerState: RegisterState, message: String? = null){
        _uiState.update {
            it.copy(
                registerState = registerState,
                message = message ?: it.message
            )
        }
    }
    private fun changeSignInState(signInState: SignInState,message: String? = null){
        _uiState.update {
            it.copy(
                signInState = signInState,
                message = message ?: it.message
            )
        }
    }
}


class AuthViewModelFactory(private val dbRepository: DbRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)){
            return AuthViewModel(dbRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}


