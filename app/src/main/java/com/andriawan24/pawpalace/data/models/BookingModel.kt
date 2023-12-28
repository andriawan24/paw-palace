package com.andriawan24.pawpalace.data.models

data class BookingModel(
    val id: String,
    val petShop: PetShopModel,
    val petOwner: UserModel,

)
