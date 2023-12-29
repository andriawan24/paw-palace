package com.andriawan24.pawpalace.features.history.presenters

import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.andriawan24.pawpalace.adapters.HistoryItemAdapter
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.databinding.FragmentHistoryBinding
import com.andriawan24.pawpalace.features.history.viewmodels.HistoryVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding, HistoryVM>(), HistoryItemAdapter.OnClickListener {

    private val adapter = HistoryItemAdapter(this)
    override val viewModel: HistoryVM by viewModels()
    override val binding: FragmentHistoryBinding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        setFragmentResultListener(HistoryGiveRatingDialogFragment.REQUEST_RATING_KEY) { _, bundle ->
            if (bundle.getBoolean(HistoryGiveRatingDialogFragment.REQUEST_RATING_KEY)) {
                viewModel.initData()
            }
        }
        binding.recyclerViewHistory.adapter = adapter
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        viewModel.initData()
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.getHistorySuccess.collectLatest {
                adapter.setData(it)
            }
        }

        lifecycleScope.launch {
            viewModel.getHistoryLoading.collectLatest {
                // TODO: Handle Loading
            }
        }

        lifecycleScope.launch {
            viewModel.getHistoryError.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onHistoryItemClicked(booking: BookingModel) {
        findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToHistoryDetailDialogFragment(booking))
    }

    override fun onGiveRatingButtonClicked(booking: BookingModel) {
        findNavController().navigate(HistoryFragmentDirections.actionHistoryFragmentToHistoryGiveRatingDialogFragment(booking))
    }
}