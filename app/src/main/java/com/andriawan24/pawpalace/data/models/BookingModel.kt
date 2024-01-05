package com.andriawan24.pawpalace.data.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class BookingModel(
    val id: String,
    val petShop: PetShopModel,
    val petOwner: UserModel,
    val startDate: Date,
    val endDate: Date,
    val createdDate: Date = Date(),
    val description: String,
    val status: String,
    val rating: Double = 0.0
): Parcelable {
    enum class Status(val statusName: String) {
        PENDING("pending"), PROCESS("process"), DONE("done"), REVIEWED("reviewed")
    }

    companion object {
        const val REFERENCE_NAME = "bookings"
        fun from(document: DocumentSnapshot): BookingModel {
            val petOwner = document.get("petOwner") as? HashMap<*, *>
            val petShop = document.get("petShop") as? HashMap<*, *>

            return BookingModel(
                id = document.id,
                petShop = PetShopModel(
                    id = petShop?.get("id").toString(),
                    name = petShop?.get("name").toString(),
                    description = petShop?.get("description").toString(),
                    userId = petShop?.get("userId").toString(),
                    location = petShop?.get("location").toString(),
                    dailyPrice = petShop?.get("dailyPrice").toString().toIntOrNull()
                        ?: 0,
                    slot = petShop?.get("slot").toString().toIntOrNull() ?: 0,
                ),
                petOwner = UserModel(
                    id = petOwner?.get("id").toString(),
                    name = petOwner?.get("name").toString(),
                    email = petOwner?.get("email").toString(),
                    phoneNumber = petOwner?.get("phoneNumber").toString(),
                    location = petOwner?.get("location").toString(),
                ),
                startDate = document.getDate("startDate") ?: Date(),
                endDate = document.getDate("endDate") ?: Date(),
                createdDate = document.getDate("createdDate") ?: Date(),
                description = document.getString("description").orEmpty(),
                status = document.getString("status").orEmpty(),
                rating = document.getDouble("rating") ?: 0.0
            )
        }
    }
}
