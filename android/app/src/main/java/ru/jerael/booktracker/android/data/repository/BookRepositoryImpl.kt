package ru.jerael.booktracker.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.jerael.booktracker.android.data.local.dao.BookDao
import ru.jerael.booktracker.android.data.mappers.toBook
import ru.jerael.booktracker.android.data.mappers.toBookEntity
import ru.jerael.booktracker.android.data.remote.api.BookApiService
import ru.jerael.booktracker.android.data.remote.dto.BookCreationDto
import ru.jerael.booktracker.android.domain.model.Book
import ru.jerael.booktracker.android.domain.model.BookCreationPayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val dao: BookDao,
    private val api: BookApiService
) : BookRepository {
    override fun getBooks(): Flow<List<Book>> {
        return dao.getAll().map { entities -> entities.map { it.toBook() } }
    }

    override suspend fun refreshBooks(): Result<Unit> {
        return runCatching {
            val booksDto = api.getBooks()
            val bookEntities = booksDto.map { it.toBookEntity() }
            dao.clearAndInsert(bookEntities)
        }
    }

    override suspend fun addBook(bookCreationPayload: BookCreationPayload): Result<String> {
        return runCatching {
            val bookCreationDto = BookCreationDto(
                title = bookCreationPayload.title,
                author = bookCreationPayload.author
            )
            val dto = api.addBook(bookCreationDto, bookCreationPayload.coverFile)
            dao.upsert(dto.toBookEntity())
            dto.id
        }
    }

    override fun getBookById(id: String): Flow<Book?> {
        return dao.getBookById(id).map { it?.toBook() }
    }

    override suspend fun refreshBookById(id: String): Result<Unit> {
        return runCatching {
            val bookDto = api.getBookById(id)
            val bookEntity = bookDto.toBookEntity()
            dao.upsert(bookEntity)
        }
    }
}