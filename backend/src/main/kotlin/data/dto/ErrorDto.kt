package ru.jerael.booktracker.backend.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val code: String,
    val message: String
)
