package ru.jerael.booktracker.backend.domain.model

import java.util.*

data class Book(
    val id: UUID,
    val title: String,
    val author: String,
    val coverPath: String?
)