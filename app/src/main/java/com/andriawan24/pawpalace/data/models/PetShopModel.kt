package com.andriawan24.pawpalace.data.models

data class PetShopModel(
    val id: String,
    val userId: String,
    val description: String,
    val location: String,
    val dailyPrice: Int,
    val slot: Int,
    val name: String,
    val rating: Double = 0.0
)
