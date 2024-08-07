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
import com.mdev1008.nutriscanandroiddev.utils.greet
import com.mdev1008.nutriscanandroiddev.utils.hideProgressBar
import com.mdev1008.nutriscanandroiddev.utils.logger
import com.mdev1008.nutriscanandroiddev.utils.showProgressBar
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
        setupMenuOptions()
        setupScanButton()
        setupDemoItemButton()
        setupRecyclerView()
        manageUiState()
    }

    private fun setupDemoItemButton(){
        activity?.showProgressBar()
        viewBinding.fabGetDemoItem.setOnClickListener {
            viewModel.emit(HomePageEvent.FetchProductDetails(DemoItems.getRandomItem()))
        }
    }

    private fun manageUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect{state ->
                when(state.productDetailsFetchState){
                    ProductDetailsFetchState.LOADING -> {
                        logger(state.productDetailsFetchState.name)
                        activity?.showProgressBar()
                    }
                    ProductDetailsFetchState.SUCCESS -> {
                        logger(state.productDetailsFetchState.name)
                        findNavController().navigate(R.id.action_home_page_to_product_details_page)
                    }
                    ProductDetailsFetchState.FAILURE -> {
                        logger(state.productDetailsFetchState.name)
                        view?.showSnackBar(state.message.toString())
                    }
                    ProductDetailsFetchState.NOT_STARTED -> {
                        activity?.hideProgressBar()
                    }
                }

                when(state.userDetailsFetchState){
                    UserDetailsFetchState.LOADING -> {
                        logger(state.userDetailsFetchState.name)
                        activity?.showProgressBar()
                    }
                    UserDetailsFetchState.SUCCESS -> {
                        logger(state.userDetailsFetchState.name)
                        updateSearchHistory(state.searchHistory)
                    }
                    UserDetailsFetchState.FAILURE -> {
                        logger(state.userDetailsFetchState.name)
                    }
                    UserDetailsFetchState.NOT_STARTED -> {

                        activity?.hideProgressBar()
                        logger(state.userDetailsFetchState.name)
                        logger(state.user?.isProfileCompleted.toString())
                        if (state.user == null){
                            findNavController().navigate(R.id.action_home_page_to_sign_in_page)
                        }
                        if (state.user?.isProfileCompleted == false){
                            findNavController().navigate(R.id.action_home_page_to_profile_page)
                        }
                    }
                }

            }
        }
    }

    private fun setupRecyclerView() {
        logger("building recycler view")
        val searchHistory = viewModel.uiState.value.searchHistory
        val searchHistoryAdapter = SearchHistoryAdapter(
            viewModel,
            searchHistory,
            onShowProgress = activity?.showProgressBar(),
            onHideProgress = activity?.hideProgressBar()
        )
        viewBinding.rvSearchHistory.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.rvSearchHistory.adapter = searchHistoryAdapter
    }
    private fun updateSearchHistory(searchHistory: List<SearchHistoryItem>){
        logger("updating search history")
        val adapter = viewBinding.rvSearchHistory.adapter as SearchHistoryAdapter
        adapter.updateData(searchHistory)
    }

    private fun setupScanButton(){
        viewBinding.fabScanProduct.setOnClickListener { scanButton ->
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
        val userName = viewModel.uiState.value.user?.userName
        val appCompatActivity = activity as AppCompatActivity
        viewBinding.mtbHomePage.let { materialToolbar ->
            appCompatActivity.setSupportActionBar(materialToolbar)
            materialToolbar.title = userName?.greet()
            materialToolbar.setTitleTextColor(Color.WHITE)
        }
        logger("building HomePage Menu")
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


