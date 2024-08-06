package com.mdev1008.nutriscanandroiddev.pages.homepage

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mdev1008.nutriscanandroiddev.NutriScanApplication
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.databinding.FragmentHomePageBinding
import com.mdev1008.nutriscanandroiddev.models.data.SearchHistoryItem
import com.mdev1008.nutriscanandroiddev.utils.BarcodeScanner
import com.mdev1008.nutriscanandroiddev.utils.DemoItems
import com.mdev1008.nutriscanandroiddev.utils.Resource
import com.mdev1008.nutriscanandroiddev.utils.logger
import com.mdev1008.nutriscanandroiddev.utils.showSnackBar
import kotlinx.coroutines.launch

class HomePage : Fragment() {

    private val viewModel by activityViewModels<HomePageViewModel> {
        val nutriScanApplication = requireActivity().application as NutriScanApplication
        HomePageViewModelFactory(
            apiRepository = nutriScanApplication.apiRepository,
            dbRepository = nutriScanApplication.dbRepository
        )
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
        setupScanButton()
        setupDemoItemButton()
        setupRecyclerView()
        manageUiState()
    }

    private fun setupDemoItemButton(){
        viewBinding.fabGetDemoItem.setOnClickListener {
            viewModel.emit(HomePageEvent.FetchProductDetails(DemoItems.getRandomItem()))
        }
    }

    private fun manageUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect{state ->
                when(state.productDetailsFetchState){
                    ProductDetailsFetchState.LOADING -> {}
                    ProductDetailsFetchState.SUCCESS -> {
                        findNavController().navigate(R.id.action_home_page_to_product_details_page)
                    }
                    ProductDetailsFetchState.FAILURE -> {
                        view?.showSnackBar(state.message.toString())
                    }
                    ProductDetailsFetchState.NOT_STARTED -> {}
                }

                when(state.userDetailsFetchState){
                    UserDetailsFetchState.LOADING -> {}
                    UserDetailsFetchState.SUCCESS -> {
                        updateSearchHistory(state.searchHistory)
                        setupMenuOptions()
                    }
                    UserDetailsFetchState.FAILURE -> {}
                    UserDetailsFetchState.NOT_STARTED -> {
                        if (state.user == null){
                            findNavController().navigate(R.id.action_home_page_to_sign_in_page)
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val searchHistory = viewModel.uiState.value.searchHistory
        val searchHistoryAdapter = SearchHistoryAdapter(viewModel, searchHistory)
        viewBinding.rvSearchHistory.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.rvSearchHistory.adapter = searchHistoryAdapter
    }
    private fun updateSearchHistory(searchHistory: List<SearchHistoryItem>){
        val adapter = viewBinding.rvSearchHistory.adapter as SearchHistoryAdapter
        adapter.updateData(searchHistory)
    }

    private fun setupScanButton(){
        viewBinding.fabScanProduct.setOnClickListener {
            BarcodeScanner.startScan(requireContext()){result ->
                when(result){
                    is Resource.Success -> {
                        logger("success")
                        result.data?.let {
                            viewModel.emit(HomePageEvent.FetchProductDetails(it))
                        }
                    }
                    is Resource.Failure ->{
                        logger("failure")
                        result.message?.let { view?.showSnackBar(it) }
                    }
                }
            }
        }
    }

    private fun setupMenuOptions() {
        val appCompatActivity = activity as AppCompatActivity
        viewBinding.mtbHomePage.let { materialToolbar ->
            appCompatActivity.setSupportActionBar(materialToolbar)
            materialToolbar.title = viewModel.uiState.value.user?.userName
            materialToolbar.setTitleTextColor(Color.WHITE)
        }
        logger("Building HomePage Menu")
        appCompatActivity.addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.homepage_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.mi_sign_out -> {
                        viewModel.emit(HomePageEvent.SignOut)
                        true
                    }
                    R.id.mi_profile ->{
                        findNavController().navigate(R.id.action_home_page_to_profile_page)
                        true
                    }
                    else -> false
                }
            }
        },viewLifecycleOwner)
    }
}


