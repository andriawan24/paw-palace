package com.andriawan24.pawpalace.features.home.viewmodels

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andriawan24.pawpalace.data.local.PawPalaceDatastore
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val dataStore: PawPalaceDatastore
): ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _navigateToOnboarding = MutableSharedFlow<None>()
    val navigateToOnboarding = _navigateToOnboarding.asSharedFlow()

    private val _isLoadingHome = MutableStateFlow(false)
    val isLoadingHome = _isLoadingHome.asStateFlow()

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
}