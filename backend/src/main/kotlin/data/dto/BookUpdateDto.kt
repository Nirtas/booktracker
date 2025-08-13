package ru.jerael.booktracker.backend.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BookUpdateDto(
    val title: String,
    val author: String
)
