package com.andriawan24.pawpalace.features.history.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryDetailDialogVM @Inject constructor(): ViewModel() {

    private val db = Firebase.firestore

    private val _closeButtonClicked = MutableSharedFlow<None>()
    val closeButtonClicked = _closeButtonClicked.asSharedFlow()

    private val _positiveButtonClicked = MutableSharedFlow<None>()
    val positiveButtonClicked = _positiveButtonClicked.asSharedFlow()

    private val _onError = MutableSharedFlow<String>()
    val onError = _onError.asSharedFlow()

    fun onApproveBookingClicked(booking: BookingModel) {
        viewModelScope.launch {
            try {
                db.collection(BookingModel.REFERENCE_NAME)
                    .document(booking.id)
                    .update("status", BookingModel.Status.PROCESS.statusName)
                    .await()

                _positiveButtonClicked.emit(None)
            } catch (e: Exception) {
                Timber.e(e)
                _onError.emit(e.localizedMessage.orEmpty())
            }
        }
    }

    fun onCompleteBookingClicked(booking: BookingModel) {
        viewModelScope.launch {
            try {
                db.collection(BookingModel.REFERENCE_NAME)
                    .document(booking.id)
                    .update("status", BookingModel.Status.DONE.statusName)
                    .await()

                _positiveButtonClicked.emit(None)
            } catch (e: Exception) {
                Timber.e(e)
                _onError.emit(e.localizedMessage.orEmpty())
            }
        }
    }

    fun onCloseButtonClicked() {
        viewModelScope.launch {
            _closeButtonClicked.emit(None)
        }
    }
}