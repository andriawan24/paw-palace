package com.andriawan24.pawpalace.data.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
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
    val fromSender: String
): Parcelable {

    @Parcelize
    data class PetShop(
        val id: String,
        val userId: String,
        val name: String
    ): Parcelable {
        companion object {
            fun from(document: DocumentSnapshot): PetShop {
                return PetShop(
                    id = document.id,
                    userId = document.getString("userId").orEmpty(),
                    name = document.getString("name").orEmpty()
                )
            }
        }
    }

    companion object {
        const val REFERENCE_NAME = "chats"

        fun from(document: DocumentSnapshot): ChatModel {
            val sender = document.get("sender") as? HashMap<*, *>
            val receiver = document.get("petShop") as? HashMap<*, *>

            val senderModel = UserModel(
                id = sender?.get("id").toString(),
                name = sender?.get("name").toString(),
                email = sender?.get("email").toString(),
                phoneNumber = sender?.get("phoneNumber").toString(),
                location = sender?.get("location").toString(),
            )

            val receiverModel = PetShop(
                id = receiver?.get("id").toString(),
                name = receiver?.get("name").toString(),
                userId = receiver?.get("userId").toString()
            )

            return ChatModel(
                id = document.id,
                sender = senderModel,
                petShop = receiverModel,
                text = document.getString("text").orEmpty(),
                createdAt = document.getDate("createdAt") ?: Date(),
                read = document.getBoolean("read") ?: false,
                senderId = document.getString("senderId").orEmpty(),
                petShopId = document.getString("petShopId").orEmpty(),
                fromSender = document.getString("fromSender").orEmpty()
            )
        }
    }
}