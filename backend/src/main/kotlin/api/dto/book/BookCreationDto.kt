package ru.jerael.booktracker.backend.api.dto.book

import kotlinx.serialization.Serializable

@Serializable
data class BookCreationDto(
    val title: String,
    val author: String,
    val status: String,
    val genreIds: List<Int>
)
