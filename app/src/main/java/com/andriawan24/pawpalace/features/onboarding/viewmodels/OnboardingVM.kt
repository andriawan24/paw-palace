package com.andriawan24.pawpalace.features.onboarding.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingVM @Inject constructor(): ViewModel() {

    private val auth = Firebase.auth

    private val _navigateToHome = MutableSharedFlow<None>()
    val navigateToHome = _navigateToHome.asSharedFlow()

    fun checkUserLogin() {
        viewModelScope.launch {
            if (auth.currentUser != null) {
                _navigateToHome.emit(None)
            }
        }
    }
}