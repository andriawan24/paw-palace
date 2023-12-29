package com.andriawan24.pawpalace.features.history.presenters

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.databinding.FragmentHistoryDetailDialogBinding
import com.andriawan24.pawpalace.databinding.FragmentHistoryGiveRatingDialogBinding
import com.andriawan24.pawpalace.features.history.viewmodels.HistoryDetailGiveRatingVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HistoryGiveRatingDialogFragment: DialogFragment() {

    private val viewModel: HistoryDetailGiveRatingVM by viewModels()
    private val args: HistoryDetailDialogFragmentArgs by navArgs()
    private val binding: FragmentHistoryGiveRatingDialogBinding by lazy {
        FragmentHistoryGiveRatingDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme).apply {
            initObserver()
            binding.textViewName.text = args.booking.petShop.name
            binding.buttonSubmit.setOnClickListener {
                val rating = binding.ratingBar.rating
                Timber.d("Rating now $rating")
                viewModel.giveRating(rating.toDouble(), args.booking)
            }
            setView(binding.root)
        }.create()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.getHistorySuccess.collectLatest {
                setFragmentResult(REQUEST_RATING_KEY, bundleOf(REQUEST_RATING_KEY to true))
                findNavController().navigateUp()
            }
        }

        lifecycleScope.launch {
            viewModel.getHistoryError.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_RATING_KEY = "REQUEST_RATING_KEY"
    }
}