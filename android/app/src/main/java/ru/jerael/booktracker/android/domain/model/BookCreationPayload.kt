package ru.jerael.booktracker.android.domain.model

import java.io.File

data class BookCreationPayload(
    val title: String,
    val author: String,
    val coverFile: File?
)
