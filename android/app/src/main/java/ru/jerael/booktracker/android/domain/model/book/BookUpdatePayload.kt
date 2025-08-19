package ru.jerael.booktracker.android.domain.model.book

import java.io.File

data class BookUpdatePayload(
    val id: String,
    val title: String,
    val author: String,
    val coverFile: File?,
    val status: BookStatus,
    val genreIds: List<Int>
)
