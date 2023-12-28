package com.andriawan24.pawpalace.features.booking.viewmodels

import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BookingFormVM @Inject constructor(private val datastore: PawPalaceDatastore): ViewModel() {

    private val db = Firebase.firestore
    private val _startDateState = MutableStateFlow(Date())
    val startDateState = _startDateState.asStateFlow()

    private val _endDateState = MutableStateFlow(Date())
    val endDateState = _endDateState.asStateFlow()

    private val _isBookingLoading = MutableStateFlow(false)
    val isBookingLoading = _isBookingLoading.asStateFlow()

    private val _isBookingSuccess = MutableSharedFlow<None>()
    val isBookingSuccess = _isBookingSuccess.asSharedFlow()

    private val _isBookingError = MutableSharedFlow<String>()
    val isBookingError = _isBookingError.asSharedFlow()

    private val _navigateToOnboarding = MutableSharedFlow<None>()
    val navigateToOnboarding = _navigateToOnboarding.asSharedFlow()

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

    fun saveBooking(petShop: PetShopModel, startDate: Date, endDate: Date, description: String) {
        viewModelScope.launch {
            _isBookingLoading.emit(true)
            try {
                val user = Firebase.auth.currentUser
                if (user != null) {
                    val bookingDocument = db.collection("bookings")
                        .document()

                    datastore.getCurrentUser().first()?.let {
                        val booking = BookingModel(
                            id = bookingDocument.id,
                            petShop = petShop,
                            petOwner = it,
                            startDate = startDate,
                            endDate = endDate,
                            description = description
                        )

                        bookingDocument.set(booking).await()
                        _isBookingLoading.emit(false)
                        _isBookingSuccess.emit(None)
                    }
                } else {
                    Firebase.auth.signOut()
                    datastore.setCurrentPetShop(null)
                    datastore.setCurrentUser(null)
                    _navigateToOnboarding.emit(None)
                }
            } catch (e: Exception) {
                Timber.e(e)
                _isBookingLoading.emit(false)
                _isBookingError.emit(e.localizedMessage.orEmpty())
            }
        }
    }
}