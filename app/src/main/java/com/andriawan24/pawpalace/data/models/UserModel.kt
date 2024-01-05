package com.andriawan24.pawpalace.data.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    val name: String,
    val email: String,
    val id: String,
    val phoneNumber: String,
    val location: String = ""
): Parcelable {
    companion object {
        const val REFERENCE_NAME = "users"
        fun from(document: DocumentSnapshot): UserModel {
            return UserModel(
                name = document.getString("name").orEmpty(),
                email = document.getString("email").orEmpty(),
                id = document.getString("id").orEmpty(),
                phoneNumber = document.getString("phoneNumber").orEmpty()
            )
        }
    }
}
