package com.andriawan24.pawpalace.features.register.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RegisterPetOwnerVM @Inject constructor(
    private val datastore: PawPalaceDatastore
): ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _isRegisterLoading = MutableStateFlow(false)
    val isRegisterLoading = _isRegisterLoading.asStateFlow()

    private val _registerError = MutableSharedFlow<String>()
    val registerError = _registerError.asSharedFlow()

    private val _registerSuccess = MutableSharedFlow<None>()
    val registerSuccess = _registerSuccess.asSharedFlow()

    fun register(name: String, phoneNumber: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                auth.currentUser?.let { user ->
                    // Update User Profile
                    val updateProfile = userProfileChangeRequest { displayName = name }
                    user.updateProfile(updateProfile).await()

                    // Store User Profile
                    val userModel = UserModel(
                        user.displayName.orEmpty(),
                        user.email.orEmpty(),
                        user.uid,
                        phoneNumber
                    )

                    db.collection("users")
                        .document(user.uid)
                        .set(userModel)
                        .await()

                    datastore.setCurrentUser(userModel)
                }

                _isRegisterLoading.emit(false)
                _registerSuccess.emit(None)
            } catch (e: Exception) {
                Timber.e(e)
                _isRegisterLoading.emit(false)
                _registerError.emit(e.message.orEmpty())
            }
        }
    }
}