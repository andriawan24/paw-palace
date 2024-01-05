package com.andriawan24.pawpalace.features.register.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
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
import javax.inject.Inject

@HiltViewModel
class CompleteRegisterPetShopVM @Inject constructor(
    private val datastore: PawPalaceDatastore
) : ViewModel() {

    private val db = Firebase.firestore

    private val _isCompleteRegisterLoading = MutableStateFlow(false)
    val isCompleteRegisterLoading = _isCompleteRegisterLoading.asStateFlow()

    private val _completeRegisterError = MutableSharedFlow<String>()
    val completeRegisterError = _completeRegisterError.asSharedFlow()

    private val _completeRegisterSuccess = MutableSharedFlow<None>()
    val completeRegisterSuccess = _completeRegisterSuccess.asSharedFlow()

    fun completeRegister(
        id: String,
        description: String,
        location: String,
        dailyPrice: Int,
        slot: Int
    ) {
        viewModelScope.launch {
            _isCompleteRegisterLoading.emit(true)
            try {
                val petShop = datastore.getCurrentPetShop().first()
                db.collection("pet_shops")
                    .document(id)
                    .update(
                        "description", description,
                        "location", location,
                        "dailyPrice", dailyPrice,
                        "slot", slot,
                        "currentSlot", slot
                    )
                    .await()

                val newPetShop = petShop?.copy(
                    description = description,
                    location = location,
                    dailyPrice = dailyPrice,
                    slot = slot,
                    currentSlot = slot
                )

                datastore.setCurrentPetShop(newPetShop)
                _isCompleteRegisterLoading.emit(false)
                _completeRegisterSuccess.emit(None)
            } catch (e: Exception) {
                Timber.e(e)
                _isCompleteRegisterLoading.emit(false)
                _completeRegisterError.emit(e.localizedMessage.orEmpty())
            }
        }
    }
}