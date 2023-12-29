package com.andriawan24.pawpalace.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PetShopModel(
    val id: String,
    val userId: String,
    val description: String,
    val location: String,
    val dailyPrice: Int,
    val slot: Int,
    val name: String,
    val rating: Double = 0.0,
    val rated: Int = 0
) : Parcelable
