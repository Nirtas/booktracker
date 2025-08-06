package ru.jerael.booktracker.android.data.mappers

import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.domain.model.Book

fun BookEntity.toBook(): Book {
    return Book(id = this.id, title = this.title, author = this.author, coverUrl = this.coverUrl)
}

