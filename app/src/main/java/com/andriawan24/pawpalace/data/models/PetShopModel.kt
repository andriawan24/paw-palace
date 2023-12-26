package com.andriawan24.pawpalace.data.models

import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference

data class PetShopModel(
    val id: String,
    val userRef: DocumentReference,
    val description: String,
    val location: String,
    val dailyPrice: Int,
    val slot: Int,
    val name: String
)
