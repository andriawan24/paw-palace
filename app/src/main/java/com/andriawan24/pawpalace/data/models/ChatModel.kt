package com.andriawan24.pawpalace.data.models

import java.util.Date

data class ChatModel(
    val id: String,
    val sender: UserModel,
    val senderId: String,
    val petShopId: String,
    val petShop: PetShopModel,
    val text: String,
    val createdAt: Date = Date(),
    val read: Boolean = false,
    val fromSender: Boolean = true
)
