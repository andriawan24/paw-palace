package com.andriawan24.pawpalace.features.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import com.andriawan24.pawpalace.data.models.BookingModel
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val dataStore: PawPalaceDatastore
) : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _navigateToOnboarding = MutableSharedFlow<None>()
    val navigateToOnboarding = _navigateToOnboarding.asSharedFlow()

    private val _getPetShopsLoading = MutableStateFlow(false)
    val getPetShopsLoading = _getPetShopsLoading.asStateFlow()

    private val _getPetShopsError = MutableSharedFlow<String>()
    val getPetShopsError = _getPetShopsError.asSharedFlow()

    private val _getPetShopsSuccess = MutableSharedFlow<List<PetShopModel>>()
    val getPetShopsSuccess = _getPetShopsSuccess.asSharedFlow()

    private val _getPetOwnerLoading = MutableStateFlow(false)
    val getPetOwnerLoading = _getPetOwnerLoading.asStateFlow()

    private val _getPetOwnerError = MutableSharedFlow<String>()
    val getPetOwnerError = _getPetOwnerError.asSharedFlow()

    private val _getPetOwnerSuccess = MutableSharedFlow<Pair<List<BookingModel>, Int>>()
    val getPetOwnerSuccess = _getPetOwnerSuccess.asSharedFlow()

    private val _setPetShopMode = MutableSharedFlow<PetShopModel>()
    val setPetShopMode = _setPetShopMode.asSharedFlow()

    private val _setPetOwnerMode = MutableSharedFlow<UserModel>()
    val setPetOwnerMode = _setPetOwnerMode.asSharedFlow()

    fun initData() {
        viewModelScope.launch(Dispatchers.IO) {
            val petShop = dataStore.getCurrentPetShop().first()
            if (auth.currentUser != null) {
                val user = dataStore.getCurrentUser().first()!!
                if (petShop != null) {
                    _setPetShopMode.emit(petShop)
                } else {
                    _setPetOwnerMode.emit(user)
                }
            } else {
                auth.signOut()
                dataStore.setCurrentPetShop(null)
                dataStore.setCurrentPetShop(null)
                _navigateToOnboarding.emit(None)
                return@launch
            }
        }
    }

    fun getPetOwners(user: PetShopModel) {
        viewModelScope.launch {
            _getPetOwnerLoading.emit(true)
            try {
                val bookingDocuments = db.collection(BookingModel.REFERENCE_NAME)
                    .whereEqualTo("petShop.id", user.id)
                    .get()
                    .await()

                val bookings = bookingDocuments.documents.map {
                    BookingModel.from(it)
                }.filter {
                    it.status == BookingModel.Status.PROCESS.statusName
                }

                _getPetOwnerLoading.emit(false)
                _getPetOwnerSuccess.emit(Pair(bookings, user.slot))
            } catch (e: Exception) {
                Timber.e(e)
                _getPetOwnerLoading.emit(false)
                _getPetOwnerError.emit(e.localizedMessage.orEmpty())
            }
        }
    }

    fun getPetShops(user: UserModel) {
        viewModelScope.launch {
            _getPetShopsLoading.emit(true)
            try {
                val petShopsDocuments = db.collection(PetShopModel.REFERENCE_NAME)
                    .whereNotEqualTo("userId", user.id)
                    .get()
                    .await()

                val petShops = petShopsDocuments.documents.map {
                    PetShopModel.from(it)
                }.filter {
                    it.slot > 0
                }

                withContext(Dispatchers.Main) {
                    _getPetShopsLoading.emit(false)
                    _getPetShopsSuccess.emit(petShops)
                }
            } catch (e: Exception) {
                Timber.e(e)
                _getPetShopsLoading.emit(false)
                _getPetShopsError.emit(e.localizedMessage.orEmpty())
            }
        }
    }
}