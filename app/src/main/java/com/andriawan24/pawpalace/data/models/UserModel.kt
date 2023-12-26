package com.andriawan24.pawpalace.data.models

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val name: String,
    val email: String,
    val id: String,
    val phoneNumber: String
): Parcelable
