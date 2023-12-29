package com.andriawan24.pawpalace.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val name: String,
    val email: String,
    val id: String,
    val phoneNumber: String,
    val location: String = ""
): Parcelable
