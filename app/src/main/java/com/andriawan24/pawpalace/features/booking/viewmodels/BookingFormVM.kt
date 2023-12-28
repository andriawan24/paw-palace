package com.andriawan24.pawpalace.features.booking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BookingFormVM @Inject constructor(): ViewModel() {

    private val _startDateState = MutableStateFlow(Date())
    val startDateState = _startDateState.asStateFlow()

    private val _endDateState = MutableStateFlow(Date())
    val endDateState = _endDateState.asStateFlow()

    fun setStartDate(time: Long) {
        viewModelScope.launch {
            val date = Date(time)
            _startDateState.emit(date)
        }
    }

    fun setEndDate(time: Long) {
        viewModelScope.launch {
            val date = Date(time)
            _endDateState.emit(date)
        }
    }
}