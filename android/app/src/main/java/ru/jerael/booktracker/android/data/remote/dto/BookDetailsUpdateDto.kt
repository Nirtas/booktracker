package ru.jerael.booktracker.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookDetailsUpdateDto(
    val title: String,
    val author: String
)
