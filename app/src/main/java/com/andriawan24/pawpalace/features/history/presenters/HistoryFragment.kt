package com.andriawan24.pawpalace.features.history.presenters

import androidx.fragment.app.viewModels
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.databinding.FragmentHistoryBinding
import com.andriawan24.pawpalace.features.history.viewmodels.HistoryVM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding, HistoryVM>() {

    override val viewModel: HistoryVM by viewModels()
    override val binding: FragmentHistoryBinding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {

    }

    override fun onInitObserver() {

    }
}