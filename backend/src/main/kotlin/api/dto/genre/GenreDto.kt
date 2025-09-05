package ru.jerael.booktracker.backend.api.dto.genre

import kotlinx.serialization.Serializable

@Serializable
data class GenreDto(
    val id: Int,
    val name: String
)
