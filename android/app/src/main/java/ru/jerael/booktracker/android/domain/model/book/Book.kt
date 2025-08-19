package ru.jerael.booktracker.android.domain.model.book

import ru.jerael.booktracker.android.domain.model.genre.Genre
import java.time.Instant

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val status: BookStatus,
    val createdAt: Instant,
    val genres: List<Genre>
)
