package ru.jerael.booktracker.android.domain.model

import java.io.File

data class BookUpdatePayload(
    val id: String,
    val title: String,
    val author: String,
    val coverFile: File?
)
