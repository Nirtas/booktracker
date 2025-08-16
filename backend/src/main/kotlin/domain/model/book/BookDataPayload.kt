package ru.jerael.booktracker.backend.domain.model.book

import ru.jerael.booktracker.backend.domain.model.genre.Genre

data class BookDataPayload(
    val title: String,
    val author: String,
    val coverPath: String?,
    val status: BookStatus,
    val genres: List<Genre>
)