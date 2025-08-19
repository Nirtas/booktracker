package ru.jerael.booktracker.android.domain.model.book

import java.io.File

data class BookCreationPayload(
    val title: String,
    val author: String,
    val coverFile: File?,
    val status: BookStatus,
    val genreIds: List<Int>
)
