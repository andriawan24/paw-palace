package com.andriawan24.pawpalace.features.history.presenters

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import com.andriawan24.pawpalace.features.history.viewmodels.HistoryDetailDialogVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HistoryDetailDialogFragment: DialogFragment() {

    private val viewModel: HistoryDetailDialogVM by viewModels()
    private val args: HistoryDetailDialogFragmentArgs by navArgs()
    private val binding: FragmentHistoryDetailDialogBinding by lazy {
        FragmentHistoryDetailDialogBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        onInitObserver()
        return MaterialAlertDialogBuilder(requireContext(), theme).apply {
            binding.textViewName.text = args.booking.petShop.name

            val days = TimeUnit.MILLISECONDS.toDays(args.booking.endDate.time - args.booking.startDate.time) + 1
            binding.textViewTotalDays.text = "$days Days"

            val simpleDateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.textViewDate.text = "${simpleDateFormatter.format(args.booking.startDate)} - ${simpleDateFormatter.format(args.booking.endDate)}"

            val totalPrice = args.booking.petShop.dailyPrice * days
            val numberFormatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            binding.textViewPricePerDay.text = numberFormatter.format(args.booking.petShop.dailyPrice)
            binding.textViewTotalPrice.text = numberFormatter.format(totalPrice)
            binding.textViewDescription.text = args.booking.description

            val status = BookingModel.Status.valueOf(args.booking.status.uppercase())
            binding.textViewStatus.text = status.statusName.titleCase()
            when(status) {
                BookingModel.Status.PENDING -> {
                    binding.textViewStatus.setTextColor(getColor(R.color.yellow))
                    binding.cardViewStatus.setBackgroundColor(getColor(R.color.yellow_background))
                }
                BookingModel.Status.PROCESS -> {
                    binding.textViewStatus.setTextColor(getColor(R.color.yellow))
                    binding.cardViewStatus.setBackgroundColor(getColor(R.color.yellow_background))
                }
                BookingModel.Status.DONE -> {
                    binding.textViewStatus.setTextColor(getColor(R.color.green))
                    binding.cardViewStatus.setBackgroundColor(getColor(R.color.green_background))
                }
                BookingModel.Status.REVIEWED -> {
                    binding.textViewStatus.setTextColor(getColor(R.color.green))
                    binding.cardViewStatus.setBackgroundColor(getColor(R.color.green_background))
                }
            }

            if (args.isPetShop) {
                when (status) {
                    BookingModel.Status.PENDING -> {
                        binding.linearLayoutButtonContainer.visibility = VISIBLE
                        binding.buttonPositive.text = "Approve"
                        binding.buttonPositive.setOnClickListener {
                            viewModel.onApproveBookingClicked(args.booking)
                        }
                        binding.buttonClose.setOnClickListener {
                            viewModel.onCloseButtonClicked()
                        }
                    }

                    BookingModel.Status.PROCESS -> {
                        binding.linearLayoutButtonContainer.visibility = VISIBLE
                        binding.buttonPositive.text = "Complete"
                        binding.buttonPositive.setOnClickListener {
                            viewModel.onCompleteBookingClicked(args.booking)
                        }
                        binding.buttonClose.setOnClickListener {
                            viewModel.onCloseButtonClicked()
                        }
                    }

                    BookingModel.Status.DONE -> {
                        binding.linearLayoutButtonContainer.visibility = GONE
                    }

                    BookingModel.Status.REVIEWED -> {
                        binding.linearLayoutButtonContainer.visibility = GONE
                    }
                }
            }

            setView(binding.root)
        }.create()
    }

    private fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.positiveButtonClicked.collectLatest {
                setFragmentResult(
                    HistoryGiveRatingDialogFragment.REQUEST_RATING_KEY, bundleOf(
                        HistoryGiveRatingDialogFragment.REQUEST_RATING_KEY to true)
                )
                findNavController().navigateUp()
            }
        }

        lifecycleScope.launch {
            viewModel.closeButtonClicked.collectLatest {
                findNavController().navigateUp()
            }
        }

        lifecycleScope.launch {
            viewModel.onError.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun String.titleCase(): String {
        return this.replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }

    private fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(binding.root.context, colorRes)
    }
}