package com.mdev1008.nutriscanandroiddev.presentation.home_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.data.model.DietaryRestriction
import com.mdev1008.nutriscanandroiddev.data.model.UserProfileDetails
import com.mdev1008.nutriscanandroiddev.data.model.getPalmOilStatus
import com.mdev1008.nutriscanandroiddev.data.model.getVeganStatus
import com.mdev1008.nutriscanandroiddev.data.model.getVegetarianStatus
import com.mdev1008.nutriscanandroiddev.databinding.FragmentHomePageBinding
import com.mdev1008.nutriscanandroiddev.domain.model.RecommendedProductForView
import com.mdev1008.nutriscanandroiddev.domain.model.SearchHistoryItemForView
import com.mdev1008.nutriscanandroiddev.utils.Status
import com.mdev1008.nutriscanandroiddev.utils.getIcon
import com.mdev1008.nutriscanandroiddev.utils.hide
import com.mdev1008.nutriscanandroiddev.utils.infoLogger
import com.mdev1008.nutriscanandroiddev.utils.show
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
        viewModel.emit(HomePageEvent.GetUserDetails)
        viewModel.emit(HomePageEvent.GetSearchHistory)
        viewBinding.apply {
            rvHpSearchHistory.adapter = SearchHistoryAdapter(emptyList()){ productId ->
                val bundle = Bundle().apply {
                    putString(getString(R.string.productId), productId)
                }
                findNavController().navigate(R.id.action_home_page_to_product_details_page, bundle)
            }
            rvHpSearchHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            rvRecommendedProduct.adapter = RecommendedProductAdapter(emptyList()){ productId ->
                val bundle = Bundle().apply {
                    putString(getString(R.string.productId), productId)
                }
                findNavController().navigate(R.id.action_home_page_to_product_details_page, bundle)
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
                            infoLogger("searchHistory has ${state.searchHistory.size} items")
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
            val greeting = "Hello, ${user.userDetails.userName.capitalize(Locale.current)}"
            tvHpUserName.text = greeting
            val dietaryRestrictions = user.userRestrictions.map { it.dietaryRestriction }
            val palmOilStatus = dietaryRestrictions.getPalmOilStatus()
            val veganStatus = dietaryRestrictions.getVeganStatus()
            val vegetarianStatus = dietaryRestrictions.getVegetarianStatus()
            infoLogger(palmOilStatus.heading)
            infoLogger(veganStatus.heading)
            infoLogger(vegetarianStatus.heading)
            ivHpUserPalmOil.setImageDrawable(palmOilStatus.getIcon(requireContext(), palmOilStatus != DietaryRestriction.PALM_OIL_STATUS_UNKNOWN))
            ivHpUserVegan.setImageDrawable(veganStatus.getIcon(requireContext(), veganStatus != DietaryRestriction.VEGAN_STATUS_UNKNOWN))
            ivHpUserVegetarian.setImageDrawable(vegetarianStatus.getIcon(requireContext(), vegetarianStatus != DietaryRestriction.VEGETARIAN_STATUS_UNKNOWN))
        }
    }

    private fun updateSearchHistory(searchHistory: List<SearchHistoryItemForView>){
        if (searchHistory.isEmpty()){
            viewBinding.apply {
                rvHpSearchHistory.hide()
                tvRecentsMessage.text = getString(R.string.no_items_scanned_yet)
                tvRecentsMessage.show()
            }
        }else{
            viewBinding.apply {
                rvHpSearchHistory.show()
                tvRecentsMessage.hide()
            }
        }
        val adapter = viewBinding.rvHpSearchHistory.adapter as SearchHistoryAdapter
        adapter.updateList(searchHistory)
    }

    private fun updateRecommendedProducts(recommendedProducts: List<RecommendedProductForView>){
        val adapter = viewBinding.rvRecommendedProduct.adapter as RecommendedProductAdapter
        adapter.updateList(recommendedProducts)
    }
}


