package com.andriawan24.pawpalace.features.profile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileVM @Inject constructor(private val datastore: PawPalaceDatastore): ViewModel() {

    private val auth = Firebase.auth

    private val _navigateToOnboarding = MutableSharedFlow<None>()
    val navigateToOnboarding = _navigateToOnboarding.asSharedFlow()

    private val _isProfileLoading = MutableStateFlow(false)
    val isProfileLoading = _isProfileLoading.asStateFlow()

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
}