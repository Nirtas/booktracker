package ru.jerael.booktracker.backend.domain.model.book

data class BookCreationPayload(
    val title: String,
    val author: String,
    val coverPath: String?
)
