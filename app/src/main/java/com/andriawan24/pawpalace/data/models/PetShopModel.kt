package com.andriawan24.pawpalace.data.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class PetShopModel(
    val id: String,
    val userId: String,
    val description: String = "",
    val location: String = "",
    val dailyPrice: Int = 0,
    val slot: Int = 0,
    val currentSlot: Int = 0,
    val name: String,
    val rating: Double = 0.0,
    val rated: Int = 0
) : Parcelable {
    companion object {
        const val REFERENCE_NAME = "pet_shops"
        fun from(document: DocumentSnapshot): PetShopModel {
            return PetShopModel(
                id = document.id,
                userId = document.getString("userId").orEmpty(),
                description = document.getString("description").orEmpty(),
                location = document.getString("location").orEmpty(),
                dailyPrice = document.getLong("dailyPrice")?.toInt() ?: 0,
                slot = document.getLong("slot")?.toInt() ?: 0,
                name = document.getString("name").orEmpty(),
                rating = document.getDouble("rating") ?: 0.0,
                rated = document.getLong("rated")?.toInt() ?: 0,
                currentSlot = document.getLong("currentSlot")?.toInt() ?: 0
            )
        }
    }
}
