package com.mdev1008.nutriscanandroiddev.presentation.history_page

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mdev1008.nutriscanandroiddev.databinding.FragmentHistoryPageBinding

class HistoryPage : Fragment() {
    private lateinit var viewBinding: FragmentHistoryPageBinding
    private lateinit var viewModel: HistoryPageViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewBinding = FragmentHistoryPageBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}