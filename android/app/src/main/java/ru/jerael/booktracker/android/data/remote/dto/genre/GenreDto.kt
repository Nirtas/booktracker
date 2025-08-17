package ru.jerael.booktracker.android.data.remote.dto.genre

import kotlinx.serialization.Serializable

@Serializable
data class GenreDto(
    val id: Int,
    val name: String
)
