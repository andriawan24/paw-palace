package com.andriawan24.pawpalace.features.profile.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
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
class EditProfileVM @Inject constructor(private val datastore: PawPalaceDatastore) : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _isEditProfileLoading = MutableStateFlow(false)
    val isEditProfileLoading = _isEditProfileLoading.asStateFlow()

    private val _isEditProfileSuccess = MutableSharedFlow<None>()
    val isEditProfileSuccess = _isEditProfileSuccess.asSharedFlow()

    private val _isEditProfileError = MutableSharedFlow<String>()
    val isEditProfileError = _isEditProfileError.asSharedFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)

    fun updateImageUri(imageUri: Uri?) {
        viewModelScope.launch {
            _imageUri.emit(imageUri)
        }
    }

    fun updatePetShop(
        name: String,
        phoneNumber: String,
        user: UserModel,
        location: String,
        description: String,
        dailyPrice: Int,
        slot: Int
    ) {
        viewModelScope.launch {
            _isEditProfileLoading.emit(true)
            try {
                val updateProfileRequest = userProfileChangeRequest {
                    displayName = name
                    photoUri = _imageUri.value
                }

                val currentFirebaseUser = auth.currentUser
                val petShop = datastore.getCurrentPetShop().first()
                if (currentFirebaseUser != null && petShop != null) {
                    currentFirebaseUser
                        .updateProfile(updateProfileRequest)
                        .await()

                    val newUser = user.copy(
                        phoneNumber = phoneNumber
                    )

                    val newPetShop = petShop.copy(
                        name = name,
                        description = description,
                        dailyPrice = dailyPrice,
                        slot = slot,
                        location = location
                    )

                    db.collection(UserModel.REFERENCE_NAME)
                        .document(currentFirebaseUser.uid)
                        .update("phoneNumber", newUser.phoneNumber)
                        .await()

                    db.collection(PetShopModel.REFERENCE_NAME)
                        .document(petShop.id)
                        .update(
                            "name", newPetShop.name,
                            "location", newPetShop.location,
                            "description", newPetShop.description,
                            "slot", newPetShop.slot,
                            "dailyPrice", newPetShop.dailyPrice
                        )
                        .await()

                    datastore.setCurrentPetShop(newPetShop)
                    datastore.setCurrentUser(newUser)
                    _isEditProfileLoading.emit(false)
                    _isEditProfileSuccess.emit(None)
                } else {
                    _isEditProfileLoading.emit(false)
                    _isEditProfileError.emit("Unauthenticated Request")
                }
            } catch (e: Exception) {
                Timber.e(e)
                _isEditProfileLoading.emit(false)
                _isEditProfileError.emit(e.localizedMessage.orEmpty())
            }
        }
    }

    fun updateProfile(
        name: String,
        phoneNumber: String,
        user: UserModel
    ) {
        viewModelScope.launch {
            _isEditProfileLoading.emit(true)
            try {
                val updateProfileRequest = userProfileChangeRequest {
                    displayName = name
                    photoUri = _imageUri.value
                }

                val currentFirebaseUser = auth.currentUser
                if (currentFirebaseUser != null) {
                    currentFirebaseUser
                        .updateProfile(updateProfileRequest)
                        .await()

                    val newUser = user.copy(
                        name = name,
                        phoneNumber = phoneNumber
                    )

                    db.collection(UserModel.REFERENCE_NAME)
                        .document(currentFirebaseUser.uid)
                        .update(
                            "name", newUser.name,
                            "phoneNumber", newUser.phoneNumber
                        )
                        .await()

                    datastore.setCurrentUser(newUser)
                    _isEditProfileLoading.emit(false)
                    _isEditProfileSuccess.emit(None)
                } else {
                    _isEditProfileLoading.emit(false)
                    _isEditProfileError.emit("Unauthenticated Request")
                }
            } catch (e: Exception) {
                Timber.e(e)
                _isEditProfileLoading.emit(false)
                _isEditProfileError.emit(e.localizedMessage.orEmpty())
            }
        }
    }
}
