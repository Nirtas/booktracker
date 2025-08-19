package ru.jerael.booktracker.backend.domain.model.book

data class BookDetailsUpdatePayload(
    val title: String,
    val author: String,
    val status: BookStatus,
    val genreIds: List<Int>
)
