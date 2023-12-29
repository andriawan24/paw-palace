package com.andriawan24.pawpalace.features.history.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.data.models.UserModel
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HistoryDetailGiveRatingVM @Inject constructor() : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _getHistoryLoading = MutableStateFlow(false)
    val getHistoryLoading = _getHistoryLoading.asStateFlow()

    private val _getHistoryError = MutableSharedFlow<String>()
    val getHistoryError = _getHistoryError.asSharedFlow()

    private val _getHistorySuccess = MutableSharedFlow<None>()
    val getHistorySuccess = _getHistorySuccess.asSharedFlow()

    fun giveRating(rating: Double, booking: BookingModel) {
        viewModelScope.launch {
            _getHistoryLoading.emit(true)
            try {
                val user = auth.currentUser
                if (user == null) {
                    _getHistoryLoading.emit(false)
                    _getHistoryError.emit("User not authenticated")
                    return@launch
                }

                db.collection("bookings")
                    .document(booking.id)
                    .update(
                        "rating", rating,
                        "status", BookingModel.Status.REVIEWED.statusName,
                        "petShop.rating", FieldValue.increment(rating)
                    )
                    .await()

                db.collection("pet_shops")
                    .document(booking.petShop.id)
                    .update(
                        "rating", FieldValue.increment(rating),
                        "rated", FieldValue.increment(1)
                    )
                    .await()

                _getHistoryLoading.emit(false)
                _getHistorySuccess.emit(None)
            } catch (e: Exception) {
                Timber.e(e)
                _getHistoryLoading.emit(false)
                _getHistoryError.emit(e.localizedMessage.orEmpty())
            }
        }
    }
}