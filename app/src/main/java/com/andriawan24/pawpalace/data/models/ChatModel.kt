package com.andriawan24.pawpalace.data.models

import java.util.Date

data class ChatModel(
    val id: String,
    val sender: ChatPerson,
    val receiver: ChatPerson,
    val text: String,
    val createdAt: Date = Date(),
    val read: Boolean = false
)

data class ChatPerson(
    val id: String,
    val name: String,
    val displayPicture: String
)
