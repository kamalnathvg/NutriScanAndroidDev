package com.mdev1008.nutriscanandroiddev.pages.authpages

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.window.application
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.mdev1008.nutriscanandroiddev.NutriScanApplication
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.databinding.FragmentRegisterPageBinding
import com.mdev1008.nutriscanandroiddev.utils.hideKeyboard
import com.mdev1008.nutriscanandroiddev.utils.isValidPassword
import com.mdev1008.nutriscanandroiddev.utils.isValidUserName
import com.mdev1008.nutriscanandroiddev.utils.showSnackBar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterPage : Fragment() {

    private lateinit var viewBinding: FragmentRegisterPageBinding
    private val viewModel: AuthViewModel by activityViewModels<AuthViewModel> {
        AuthViewModelFactory((requireActivity().application as NutriScanApplication).dbRepository)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentRegisterPageBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appCompatActivity = activity as AppCompatActivity

        viewBinding.mtbRegisterPage.let { materialToolbar ->
            appCompatActivity.setSupportActionBar(materialToolbar)
            materialToolbar.title = getString(R.string.register_page_title)
            materialToolbar.setupWithNavController(findNavController())
        }

        viewBinding.tvSignIn.setOnClickListener {
            findNavController().popBackStack()
        }

        viewBinding.btnRegister.setOnClickListener {
            view.hideKeyboard()
            viewBinding.apply {
                tilRegisterUsername.error = null
                tilRegisterPassword.error = null
                tilRegisterConfirmPassword.error = null
            }
            val userName = viewBinding.tilRegisterUsername.editText?.text.toString()
            val password = viewBinding.tilRegisterPassword.editText?.text.toString()
            val confirmPassword = viewBinding.tilRegisterConfirmPassword.editText?.text.toString()
            validateRegistration(
                userName = userName,
                password = password,
                confirmPassword = confirmPassword
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect{state ->
                when(state.registerState){
                    RegisterState.LOADING -> {}
                    RegisterState.SUCCESS -> {
                        view.showSnackBar(state.message)
                        findNavController().popBackStack()
                    }
                    RegisterState.FAILURE -> {
                        view.showSnackBar(state.message)
                    }
                    RegisterState.NOT_STARTED -> {}
                }
            }
        }
    }

    private fun validateRegistration(
        userName: String,
        password: String,
        confirmPassword: String
    ) {
        val (isValidUserName, userNameMessage) = userName.isValidUserName()
        val (isValidPassword, passwordMessage) = password.isValidPassword()
        if (!isValidUserName){
            viewBinding.tilRegisterUsername.error = userNameMessage
        }else if(!isValidPassword){
            viewBinding.tilRegisterPassword.error = passwordMessage
        }else if( password != confirmPassword){
            viewBinding.tilRegisterPassword.error = getString(R.string.password_mismatch_message)
            viewBinding.tilRegisterConfirmPassword.error = getString(R.string.password_mismatch_message)
        }else{
            viewModel.emit(AuthEvent.RegisterWithUserNamePassword(userName,password))
        }
    }
}