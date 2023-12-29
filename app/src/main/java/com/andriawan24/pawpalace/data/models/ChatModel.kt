package com.andriawan24.pawpalace.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class ChatModel(
    val id: String,
    val sender: UserModel,
    val senderId: String,
    val petShopId: String,
    val petShop: PetShop,
    val text: String,
    val createdAt: Date = Date(),
    val read: Boolean = false,
    val fromSender: Boolean = true
): Parcelable {

    @Parcelize
    data class PetShop(
        val id: String,
        val userId: String,
        val name: String
    ): Parcelable
}