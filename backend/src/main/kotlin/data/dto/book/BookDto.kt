package ru.jerael.booktracker.backend.data.dto.book

import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String?
)
