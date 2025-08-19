package ru.jerael.booktracker.android.domain.model.book

import android.net.Uri

data class BookCreationParams(
    val title: String,
    val author: String,
    val coverUri: Uri?,
    val status: BookStatus,
    val genreIds: List<Int>
)
