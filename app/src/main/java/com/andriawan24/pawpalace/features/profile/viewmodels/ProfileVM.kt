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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileVM @Inject constructor(private val datastore: PawPalaceDatastore): ViewModel() {

    private val auth = Firebase.auth

    private val _navigateToOnboarding = MutableSharedFlow<None>()
    val navigateToOnboarding = _navigateToOnboarding.asSharedFlow()

    private val _isProfileLoading = MutableStateFlow(false)
    val isProfileLoading = _isProfileLoading.asStateFlow()

    private val _setPetShopMode = MutableSharedFlow<PetShopModel>()
    val setPetShopMode = _setPetShopMode.asSharedFlow()

    private val _setPetOwnerMode = MutableStateFlow<Pair<UserModel?, Uri?>?>(null)
    val setPetOwnerMode = _setPetOwnerMode.asStateFlow()

    fun signOut() {
        viewModelScope.launch {
            _isProfileLoading.emit(true)
            auth.signOut()
            datastore.setCurrentUser(null)
            datastore.setCurrentPetShop(null)
            _isProfileLoading.emit(false)
            _navigateToOnboarding.emit(None)
        }
    }

    fun initData() {
        viewModelScope.launch(Dispatchers.IO) {
            val petShop = datastore.getCurrentPetShop().first()
            if (auth.currentUser != null) {
                if (petShop != null) {
                    _setPetShopMode.emit(petShop)
                } else {
                    val user = datastore.getCurrentUser().first()
                    _setPetOwnerMode.emit(Pair(user, auth.currentUser?.photoUrl))
                }
            } else {
                auth.signOut()
                datastore.setCurrentPetShop(null)
                datastore.setCurrentPetShop(null)
                _navigateToOnboarding.emit(None)
                return@launch
            }
        }
    }
}