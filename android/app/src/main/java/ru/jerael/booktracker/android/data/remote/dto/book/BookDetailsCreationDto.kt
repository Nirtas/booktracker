package ru.jerael.booktracker.android.data.remote.dto.book

import kotlinx.serialization.Serializable

@Serializable
data class BookDetailsCreationDto(
    val title: String,
    val author: String,
    val status: String,
    val genreIds: List<Int>
)
