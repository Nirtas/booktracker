package ru.jerael.booktracker.backend.data.dto.book

import kotlinx.serialization.Serializable
import ru.jerael.booktracker.backend.data.dto.genre.GenreDto

@Serializable
data class BookDto(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val status: String,
    val createdAt: Long,
    val genres: List<GenreDto>
)
