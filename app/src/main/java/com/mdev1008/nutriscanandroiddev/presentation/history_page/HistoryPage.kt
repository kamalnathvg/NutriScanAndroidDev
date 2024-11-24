package com.mdev1008.nutriscanandroiddev.presentation.history_page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mdev1008.nutriscanandroiddev.R
import com.mdev1008.nutriscanandroiddev.databinding.FragmentHistoryPageBinding
import com.mdev1008.nutriscanandroiddev.presentation.home_page.SearchHistoryAdapter
import com.mdev1008.nutriscanandroiddev.utils.Status
import com.mdev1008.nutriscanandroiddev.utils.showSnackBar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryPage : Fragment() {
    private lateinit var viewBinding: FragmentHistoryPageBinding
    private val viewModel: HistoryPageViewModel by activityViewModels<HistoryPageViewModel> { HistoryPageViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentHistoryPageBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onEvent(HistoryPageEvent.GetSearchHistory)
        viewBinding.rvHspList.apply {
            adapter = HistoryPageAdapter(mutableListOf()){ productId ->
                val bundle = Bundle().apply {
                    putString(getString(R.string.productId), productId)
                }
                findNavController().navigate(R.id.action_history_page_to_product_details_page, bundle)
            }
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.uiState.collect{state ->
                    when(state.searchHistoryFetchState){
                        Status.LOADING -> {}
                        Status.SUCCESS -> {
                            val adapter = viewBinding.rvHspList.adapter as HistoryPageAdapter
                            adapter.updateData(state.searchHistory)
                        }
                        Status.FAILURE -> {
                            view.showSnackBar(state.errorMessage.toString())
                        }
                        Status.IDLE -> {}
                    }
                }
            }
        }

        viewBinding.svHspSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    (viewBinding.rvHspList.adapter as HistoryPageAdapter).filter(it)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

        })

    }
}