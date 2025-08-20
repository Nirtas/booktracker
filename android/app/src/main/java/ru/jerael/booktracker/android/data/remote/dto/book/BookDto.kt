package ru.jerael.booktracker.android.data.remote.dto.book

import kotlinx.serialization.Serializable
import ru.jerael.booktracker.android.data.remote.dto.genre.GenreDto

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
