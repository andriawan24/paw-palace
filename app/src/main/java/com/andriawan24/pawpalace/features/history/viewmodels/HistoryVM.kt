package com.andriawan24.pawpalace.features.history.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Query
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
class HistoryVM @Inject constructor(): ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _getHistoryLoading = MutableStateFlow(false)
    val getHistoryLoading = _getHistoryLoading.asStateFlow()

    private val _getHistoryError = MutableSharedFlow<String>()
    val getHistoryError = _getHistoryError.asSharedFlow()

    private val _getHistorySuccess = MutableSharedFlow<List<BookingModel>>()
    val getHistorySuccess = _getHistorySuccess.asSharedFlow()

    fun initData() {
        viewModelScope.launch {
            _getHistoryLoading.emit(true)
            try {
                val user = auth.currentUser
                if (user == null) {
                    _getHistoryLoading.emit(false)
                    _getHistoryError.emit("User not authenticated")
                    return@launch
                }

                val bookingDocs = db.collection("bookings")
                    .orderBy("createdDate", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val bookings = bookingDocs.documents.map {
                    val petOwner = it.get("petOwner") as? HashMap<*, *>
                    val petShop = it.get("petShop") as? HashMap<*, *>
                    BookingModel(
                        id = it.id,
                        petShop = PetShopModel(
                            id = petShop?.get("id").toString(),
                            name = petShop?.get("name").toString(),
                            description = petShop?.get("description").toString(),
                            userId = petShop?.get("userId").toString(),
                            location = petShop?.get("location").toString(),
                            dailyPrice = petShop?.get("dailyPrice").toString().toIntOrNull()
                                ?: 0,
                            slot = petShop?.get("slot").toString().toIntOrNull() ?: 0,
                        ),
                        petOwner = UserModel(
                            id = petOwner?.get("id").toString(),
                            name = petOwner?.get("name").toString(),
                            email = petOwner?.get("email").toString(),
                            phoneNumber = petOwner?.get("phoneNumber").toString(),
                            location = petOwner?.get("location").toString(),
                        ),
                        startDate = it.getDate("startDate") ?: Date(),
                        endDate = it.getDate("endDate") ?: Date(),
                        createdDate = it.getDate("createdDate") ?: Date(),
                        description = it.getString("description").orEmpty(),
                        status = it.getString("status").orEmpty(),
                        rating = it.getDouble("rating") ?: 0.0
                    )
                }.filter {
                    it.petOwner.id == user.uid
                }

                _getHistoryLoading.emit(false)
                _getHistorySuccess.emit(bookings)
            } catch (e: Exception) {
                Timber.e(e)
                _getHistoryLoading.emit(false)
                _getHistoryError.emit(e.localizedMessage.orEmpty())
            }
        }
    }
}