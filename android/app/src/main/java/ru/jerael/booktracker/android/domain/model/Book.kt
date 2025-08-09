package ru.jerael.booktracker.android.domain.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String?
)
