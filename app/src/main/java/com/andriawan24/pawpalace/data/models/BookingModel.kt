package com.andriawan24.pawpalace.data.models

import java.util.Date

data class BookingModel(
    val id: String,
    val petShop: PetShopModel,
    val petOwner: UserModel,
    val startDate: Date,
    val endDate: Date,
    val createdDate: Date = Date(),
    val description: String,
    val status: String
)
