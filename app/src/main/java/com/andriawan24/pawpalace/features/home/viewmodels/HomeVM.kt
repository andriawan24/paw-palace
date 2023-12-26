package com.andriawan24.pawpalace.features.home.viewmodels

import androidx.lifecycle.ViewModel
import com.andriawan24.pawpalace.utils.None
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(): ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _navigateToOnboarding = MutableSharedFlow<None>()
    val navigateToOnboarding = _navigateToOnboarding.asSharedFlow()

    private val _isLoadingHome = MutableStateFlow(false)
    val isLoadingHome = _isLoadingHome.asStateFlow()

    fun initData() {

    }
}