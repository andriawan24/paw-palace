package com.andriawan24.pawpalace.features.detail.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailPetShopVM @Inject constructor(): ViewModel() {

    private val db = Firebase.firestore

    fun initData(petShopId: String) {
        viewModelScope.launch {

        }
    }
}