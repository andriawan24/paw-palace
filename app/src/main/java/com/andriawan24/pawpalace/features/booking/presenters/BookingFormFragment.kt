package com.andriawan24.pawpalace.features.booking.presenters

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.andriawan24.pawpalace.base.BaseFragment
import com.andriawan24.pawpalace.databinding.FragmentBookingFormBinding
import com.andriawan24.pawpalace.features.booking.viewmodels.BookingFormVM
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class BookingFormFragment: BaseFragment<FragmentBookingFormBinding, BookingFormVM>() {

    override val viewModel: BookingFormVM by viewModels()
    override val binding: FragmentBookingFormBinding by lazy {
        FragmentBookingFormBinding.inflate(layoutInflater)
    }

    override fun onInitViews() {
        initClickListener()
        initTextInputIconListener()
    }

    private fun initTextInputIconListener() {
        binding.editTextLayoutStartDate.setEndIconOnClickListener {
            openStartDateCalendar(viewModel.startDateState.value)
        }

        binding.editTextLayoutEndDate.setEndIconOnClickListener {
            openEndDateCalendar(viewModel.startDateState.value)
        }
    }

    private fun openEndDateCalendar(defaultDate: Date) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select End Date")
            .setSelection(defaultDate.time)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.setEndDate(it)
        }

        datePicker.show(childFragmentManager, "EndDatePickerDialog")
    }

    private fun openStartDateCalendar(defaultDate: Date) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Start Date")
            .setSelection(defaultDate.time)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.setStartDate(it)
        }

        datePicker.show(childFragmentManager, "StartDatePickerDialog")
    }

    private fun initClickListener() {
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSave.setOnClickListener {
            val startDate = viewModel.startDateState.value
            val endDate = viewModel.endDateState.value
            val description = binding.editTextDescription.text.toString()

            if (validateInput(startDate, endDate, description)) {

            }
        }
    }

    private fun validateInput(startDate: Date, endDate: Date, description: String): Boolean {
        var isValid = true

        if (validateStartEndDate(startDate, endDate)) {
            isValid = false
        }

        return isValid
    }

    private fun validateStartEndDate(startDate: Date, endDate: Date): Boolean {
        if (startDate.time > endDate.time) {
            binding.editTextLayoutDescription.isErrorEnabled = true
            binding.editTextLayoutDescription.error = "Description cannot be empty"
            return false
        }

        binding.editTextLayoutEndDate.error = null
        return true
    }

    private fun validateDescription(description: String): Boolean {
        if (description.isBlank()) {
            binding.editTextLayoutDescription.isErrorEnabled = true
            binding.editTextLayoutDescription.error = "Description cannot be empty"
            return false
        }

        binding.editTextLayoutEndDate.error = null
        return true
    }

    override fun onInitObserver() {
        lifecycleScope.launch {
            viewModel.startDateState.collectLatest {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.editTextStartDate.setText(formatter.format(it))
            }
        }

        lifecycleScope.launch {
            viewModel.endDateState.collectLatest {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.editTextEndDate.setText(formatter.format(it))
            }
        }
    }
}