package com.andriawan24.pawpalace.features.booking.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
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
class BookingConfirmationVM @Inject constructor(
    private val datastore: PawPalaceDatastore
): ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _isBookingLoading = MutableStateFlow(false)
    val isBookingLoading = _isBookingLoading.asStateFlow()

    private val _isBookingSuccess = MutableSharedFlow<None>()
    val isBookingSuccess = _isBookingSuccess.asSharedFlow()

    private val _isBookingError = MutableSharedFlow<String>()
    val isBookingError = _isBookingError.asSharedFlow()

    private val _navigateToOnboarding = MutableSharedFlow<None>()
    val navigateToOnboarding = _navigateToOnboarding.asSharedFlow()

    fun saveBooking(booking: BookingModel) {
        viewModelScope.launch {
            _isBookingLoading.emit(true)
            try {
                val user = auth.currentUser
                if (user != null) {
                    val bookingDocument = db.collection("bookings")
                        .document()

                    datastore.getCurrentUser().first()?.let {
                        db.collection("pet_shops")
                            .document(booking.petShop.id)
                            .update("slot", FieldValue.increment(-1))
                            .await()
                        val newBooking = booking.copy(
                            petShop = booking.petShop.copy(
                                slot = booking.petShop.slot - 1
                            )
                        )
                        bookingDocument.set(newBooking).await()
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