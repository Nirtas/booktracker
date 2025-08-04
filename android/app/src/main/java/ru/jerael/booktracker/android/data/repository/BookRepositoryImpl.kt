package ru.jerael.booktracker.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import ru.jerael.booktracker.android.data.local.dao.BookDao
import ru.jerael.booktracker.android.domain.model.Book
import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val dao: BookDao
) : BookRepository {
    override fun getBooks(): Flow<List<Book>> {
        val books = listOf(
            Book(id = "1", title = "Название 1", author = "Автор 1", coverUrl = null),
            Book(id = "2", title = "Название 2", author = "Автор 2", coverUrl = null),
            Book(id = "3", title = "Название 3", author = "Автор 3", coverUrl = null),
            Book(id = "4", title = "Название 4", author = "Автор 4", coverUrl = null)
        )
        return flowOf(books)
        //return dao.getAll().map { entities -> entities.map { it.toBook() } }
    }
}