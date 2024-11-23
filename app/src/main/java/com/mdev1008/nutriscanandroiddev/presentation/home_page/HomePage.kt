package com.mdev1008.nutriscanandroiddev.presentation.home_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mdev1008.nutriscanandroiddev.NutriScanApplication
import com.mdev1008.nutriscanandroiddev.databinding.FragmentHomePageBinding
import com.mdev1008.nutriscanandroiddev.data.model.User
import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.domain.model.RecommendedProductForView
import com.mdev1008.nutriscanandroiddev.domain.model.SearchHistoryItemForView
import com.mdev1008.nutriscanandroiddev.utils.Status
import com.mdev1008.nutriscanandroiddev.utils.infoLogger
import com.mdev1008.nutriscanandroiddev.utils.showSnackBar
import kotlinx.coroutines.launch

class HomePage : Fragment() {

    private val viewModel by activityViewModels<HomePageViewModel> {
        HomePageViewModel.Factory
    }
    private lateinit var viewBinding: FragmentHomePageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentHomePageBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.uiState.value.userProfileDetails == null){
            viewModel.emit(HomePageEvent.GetUserDetails)
        }
        viewBinding.apply {
            rvHpSearchHistory.adapter = SearchHistoryAdapter(emptyList()){
                //TODO: Navigate to product details page
            }
            rvHpSearchHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvRecommendedProduct.adapter = RecommendedProductAdapter(emptyList()){
                //TODO: Navigate to product details page
            }
            rvRecommendedProduct.layoutManager = GridLayoutManager(requireContext(), 2)
        }

        observeUserDetails()
        observeSearchHistory()
        observeRecommendedProducts()
    }

    private fun observeRecommendedProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect{state->
                    when(state.recommendedProductsFetchSate){
                        Status.LOADING -> {
                            infoLogger("Loading Recommended Products")
                        }
                        Status.SUCCESS -> {
                            updateRecommendedProducts(state.recommendedProducts)
                        }
                        Status.FAILURE -> {
                            view?.showSnackBar("Error fetching recommended products: ${state.errorMessage}")
                        }
                        Status.IDLE -> {}
                    }
                }
            }
        }
    }

    private fun observeSearchHistory() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect{state->
                    when(state.searchHistoryFetchState){
                        Status.LOADING -> {
                            infoLogger("Loading Search History")
                        }
                        Status.SUCCESS -> {
                            updateSearchHistory(state.searchHistory)
                        }
                        Status.FAILURE -> {
                            view?.showSnackBar("Error fetching search history: ${state.errorMessage}")
                        }
                        Status.IDLE -> {}
                    }
                }
            }
        }
    }

    private fun observeUserDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state.userDetailsFetchState) {
                        Status.LOADING -> {
                            infoLogger("Loading user details...")
                        }
                        Status.SUCCESS -> {
                            state.userProfileDetails?.let {
                                updateUserDetails(it)
                            }
                        }
                        Status.FAILURE -> {
                            view?.showSnackBar("Error fetching user details: ${state.errorMessage}")
                        }
                        Status.IDLE -> {}
                    }
                }
            }
        }
    }

    private fun updateUserDetails(user: UserProfileDetails){
        viewBinding.apply {
            tvHpUserName.text = user.userDetails.userName
            //TODO: implement rest of dietary preferences and restrictions
        }
    }

    private fun updateSearchHistory(searchHistory: List<SearchHistoryItemForView>){
        val adapter = viewBinding.rvHpSearchHistory.adapter as SearchHistoryAdapter
        adapter.updateList(searchHistory)
    }

    private fun updateRecommendedProducts(recommendedProducts: List<RecommendedProductForView>){
        val adapter = viewBinding.rvRecommendedProduct.adapter as RecommendedProductAdapter
        adapter.updateList(recommendedProducts)
    }
}


