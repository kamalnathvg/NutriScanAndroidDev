package com.mdev1008.nutriscanandroiddev.presentation.login_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.databinding.FragmentLoginPageBinding
import com.mdev1008.nutriscanandroiddev.utils.Status
import com.mdev1008.nutriscanandroiddev.utils.hideKeyboard
import com.mdev1008.nutriscanandroiddev.utils.isValidPassword
import com.mdev1008.nutriscanandroiddev.utils.isValidUserName
import com.mdev1008.nutriscanandroiddev.utils.debugLogger
import com.mdev1008.nutriscanandroiddev.utils.errorLogger
import com.mdev1008.nutriscanandroiddev.utils.showSnackBar
import kotlinx.coroutines.launch

class LoginPage : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels<LoginViewModel> {
        LoginViewModel.Factory
    }

    private lateinit var viewBinding: FragmentLoginPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentLoginPageBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val appCompatActivity = activity as AppCompatActivity
        viewBinding.mtbSignInPage.let { materialToolBar ->
            appCompatActivity.setSupportActionBar(materialToolBar)
            materialToolBar.title = getString(R.string.mtb_title_sign_in_page)
        }

        viewBinding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_page_to_register_page)
        }
        viewBinding.btnSignIn.setOnClickListener {
            view.hideKeyboard()
            viewBinding.apply {
                tilUsername.error = null
                tilPassword.error = null
            }
            val userName = viewBinding.tilUsername.editText?.text.toString()
            val password = viewBinding.tilPassword.editText?.text.toString()
            validateSignIn(userName, password)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect{state ->
                when(state.loginState){
                    Status.LOADING -> {
                        debugLogger(state.loginState.name)
                    }
                    Status.SUCCESS -> {
                        debugLogger(state.loginState.name)
//                        findNavController().navigate(R.id.action_sign_in_page_to_home_page)
                        findNavController().navigate(R.id.action_login_page_to_home_page)
                    }
                    Status.FAILURE -> {
                        errorLogger(state.loginState.name)
                        view.showSnackBar(state.errorMessage.toString(), Snackbar.LENGTH_LONG)
                    }
                    Status.IDLE -> {}
                }
            }
        }
    }

    private fun validateSignIn(userName: String, password: String) {
        val (isValidUserName, userNameMessage) = userName.isValidUserName()
        val (isValidPassword, passwordMessage) = password.isValidPassword()

        if (!isValidUserName){
            viewBinding.tilUsername.error = userNameMessage
        }else if (!isValidPassword){
            viewBinding.tilPassword.error = passwordMessage
        }else{
            viewModel.onEvent(LoginPageEvent.LoginWithUserNamePassword(userName, password))
        }
    }
}