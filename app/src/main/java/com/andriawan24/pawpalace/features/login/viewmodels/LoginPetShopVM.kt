package com.andriawan24.pawpalace.features.login.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import com.andriawan24.pawpalace.data.models.PetShopModel
import com.andriawan24.pawpalace.data.models.UserModel
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.auth
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
class LoginPetShopVM @Inject constructor(
    private val datastore: PawPalaceDatastore
) : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    private val _isAuthenticationLoading = MutableStateFlow(false)
    val isAuthenticationLoading = _isAuthenticationLoading.asStateFlow()

    private val _authenticationError = MutableSharedFlow<String>()
    val authenticationError = _authenticationError.asSharedFlow()

    private val _authenticationSuccess = MutableSharedFlow<None>()
    val authenticationSuccess = _authenticationSuccess.asSharedFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isAuthenticationLoading.emit(true)
            try {
                auth.signInWithEmailAndPassword(email, password).await()

                auth.currentUser?.let { user ->
                    val petShopDocuments = db.collection("pet_shops")
                        .whereEqualTo("userId", user.uid)
                        .get()
                        .await()

                    if (petShopDocuments.isEmpty) {
                        auth.signOut()
                        _isAuthenticationLoading.emit(false)
                        _authenticationError.emit("Pet shop not found")
                        return@launch
                    }

                    val userDocument = db.collection("users")
                        .document(user.uid)
                        .get()
                        .await()

                    val userModel = UserModel(
                        name = userDocument.getString("name").orEmpty(),
                        email = userDocument.getString("email").orEmpty(),
                        id = userDocument.getString("id").orEmpty(),
                        phoneNumber = userDocument.getString("phoneNumber").orEmpty(),
                        location = userDocument.getString("location").orEmpty()
                    )

                    val petShopDocument = petShopDocuments.first()
                    val petShop = PetShopModel(
                        id = petShopDocument.getString("id").orEmpty(),
                        userId = user.uid,
                        description = petShopDocument.getString("description").orEmpty(),
                        location = petShopDocument.getString("location").orEmpty(),
                        dailyPrice = petShopDocument.getLong("dailyPrice")?.toInt() ?: 0,
                        slot = petShopDocument.getLong("slot")?.toInt() ?: 0,
                        name = petShopDocument.getString("name").orEmpty()
                    )

                    datastore.setCurrentPetShop(petShop = petShop)
                    datastore.setCurrentUser(userModel)
                }

                _isAuthenticationLoading.emit(false)
                _authenticationSuccess.emit(None)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Timber.e(e)
                _isAuthenticationLoading.emit(false)
                _authenticationError.emit("Email/Password is not correct")
            } catch (e: Exception) {
                Timber.e(e)
                _isAuthenticationLoading.emit(false)
                _authenticationError.emit(e.message.toString())
            }
        }
    }
}