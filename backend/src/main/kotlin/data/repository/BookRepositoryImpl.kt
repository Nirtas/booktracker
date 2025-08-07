package ru.jerael.booktracker.backend.data.repository

import ru.jerael.booktracker.backend.domain.model.Book
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*

class BookRepositoryImpl : BookRepository {
    override fun getBooks(): List<Book> {
        val books = listOf(
            Book(
                id = UUID.randomUUID(),
                title = "Название 1",
                author = "Автор 1",
                coverPath = "post_img/2024/09/11/6/1726043826195950836.jpg"
            ),
            Book(
                id = UUID.randomUUID(),
                title = "Название 2",
                author = "Автор 2",
                coverPath = "post_img/2024/09/11/6/1726043826195950836.jpg"
            ),
            Book(id = UUID.randomUUID(), title = "Название 3", author = "Автор 3", coverPath = null),
            Book(id = UUID.randomUUID(), title = "Название 4", author = "Автор 4", coverPath = null)
        )
        return books
    }
}