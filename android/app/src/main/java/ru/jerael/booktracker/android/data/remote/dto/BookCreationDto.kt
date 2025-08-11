package ru.jerael.booktracker.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookCreationDto(
    val title: String,
    val author: String
)
