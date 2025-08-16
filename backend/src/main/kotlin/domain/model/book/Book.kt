package ru.jerael.booktracker.backend.domain.model.book

import ru.jerael.booktracker.backend.domain.model.genre.Genre
import java.time.Instant
import java.util.*

data class Book(
    val id: UUID,
    val title: String,
    val author: String,
    val coverPath: String?,
    val status: BookStatus,
    val createdAt: Instant,
    val genres: List<Genre>
)