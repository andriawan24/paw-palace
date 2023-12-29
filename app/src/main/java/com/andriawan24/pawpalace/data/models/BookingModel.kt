package com.andriawan24.pawpalace.data.models

import android.os.Parcelable
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
}
