package com.andriawan24.pawpalace.features.history.presenters

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.andriawan24.pawpalace.R
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.databinding.FragmentHistoryDetailDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class HistoryDetailDialogFragment: DialogFragment() {

    private val args: HistoryDetailDialogFragmentArgs by navArgs()
    private val binding: FragmentHistoryDetailDialogBinding by lazy {
        FragmentHistoryDetailDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
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
            setView(binding.root)
        }.create()
    }

    private fun String.titleCase(): String {
        return this.replaceFirstChar { it.titlecase(Locale.getDefault()) }
    }

    private fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(binding.root.context, colorRes)
    }
}