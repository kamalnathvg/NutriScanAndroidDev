package com.mdev1008.nutriscanandroiddev.pages.authpages

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mdev1008.nutriscanandroiddev.NutriScanApplication
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.databinding.FragmentSignInPageBinding
import com.mdev1008.nutriscanandroiddev.utils.isValidPassword
import com.mdev1008.nutriscanandroiddev.utils.isValidUserName
import com.mdev1008.nutriscanandroiddev.utils.logger
import com.mdev1008.nutriscanandroiddev.utils.showSnackBar
import kotlinx.coroutines.launch

class SignInPage : Fragment() {


    private val viewModel: AuthViewModel by activityViewModels<AuthViewModel> {
        AuthViewModelFactory((requireActivity().application as NutriScanApplication).dbRepository)
    }
    private lateinit var viewBinding: FragmentSignInPageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSignInPageBinding.inflate(inflater, container, false)
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
            findNavController().navigate(R.id.action_sign_in_page_to_register_page)
        }
        viewBinding.btnSignIn.setOnClickListener {
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
                when(state.signInState){
                    SignInState.LOADING -> {
                        logger(state.signInState.name)
                    }
                    SignInState.SUCCESS -> {
                        logger(state.signInState.name)
                        view.showSnackBar(state.message, Snackbar.LENGTH_LONG)
                        findNavController().navigate(R.id.action_sign_in_page_to_home_page)
                    }
                    SignInState.FAILURE -> {
                        logger(state.signInState.name)
                        view.showSnackBar(state.message, Snackbar.LENGTH_LONG)
                    }
                    SignInState.NOT_STARTED -> {}
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
            viewModel.emit(AuthEvent.SignInWithUserNamePassword(userName,password))
        }
    }
}