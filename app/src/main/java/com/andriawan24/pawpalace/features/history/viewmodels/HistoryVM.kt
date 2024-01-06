package com.andriawan24.pawpalace.features.history.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    private val datastore: PawPalaceDatastore
): ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _getHistoryLoading = MutableStateFlow(false)
    val getHistoryLoading = _getHistoryLoading.asStateFlow()

    private val _getHistoryError = MutableSharedFlow<String>()
    val getHistoryError = _getHistoryError.asSharedFlow()

    private val _getHistorySuccess = MutableSharedFlow<List<BookingModel>>()
    val getHistorySuccess = _getHistorySuccess.asSharedFlow()

    private val _setPetShopMode = MutableSharedFlow<None>()
    val setPetShopMode = _setPetShopMode.asSharedFlow()

    fun initData() {
        viewModelScope.launch {
            _getHistoryLoading.emit(true)
            try {
                val user = auth.currentUser
                val petShop = datastore.getCurrentPetShop().firstOrNull()
                if (user == null) {
                    _getHistoryLoading.emit(false)
                    _getHistoryError.emit("User not authenticated")
                    return@launch
                }

                if (petShop != null) {
                    val bookingDocs = db.collection(BookingModel.REFERENCE_NAME)
                        .whereEqualTo("petShop.id", petShop.id)
                        .orderBy("createdDate", Query.Direction.DESCENDING)
                        .get()
                        .await()

                    val bookings = bookingDocs.documents.map { bookingDoc ->
                        BookingModel.from(bookingDoc)
                    }

                    _setPetShopMode.emit(None)
                    _getHistoryLoading.emit(false)
                    _getHistorySuccess.emit(bookings)
                } else {
                    val bookingDocs = db.collection(BookingModel.REFERENCE_NAME)
                        .whereEqualTo("petOwner.id", user.uid)
                        .orderBy("createdDate", Query.Direction.DESCENDING)
                        .get()
                        .await()

                    val bookings = bookingDocs.documents.map { bookingDoc ->
                        BookingModel.from(bookingDoc)
                    }

                    _getHistoryLoading.emit(false)
                    _getHistorySuccess.emit(bookings)
                }
            } catch (e: Exception) {
                Timber.e(e)
                _getHistoryLoading.emit(false)
                _getHistoryError.emit(e.localizedMessage.orEmpty())
            }
        }
    }
}