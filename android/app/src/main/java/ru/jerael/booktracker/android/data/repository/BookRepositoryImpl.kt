package ru.jerael.booktracker.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.jerael.booktracker.android.data.local.dao.BookDao
import ru.jerael.booktracker.android.data.local.entity.BookGenresEntity
import ru.jerael.booktracker.android.data.mappers.toBook
import ru.jerael.booktracker.android.data.mappers.toBookEntity
import ru.jerael.booktracker.android.data.remote.api.BookApiService
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsCreationDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsUpdateDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDto
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.android.domain.model.book.BookUpdatePayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val dao: BookDao,
    private val api: BookApiService
) : BookRepository {
    override fun getBooks(): Flow<List<Book>> {
        return dao.getBookWithGenres().map { entities -> entities.map { it.toBook() } }
    }

    override fun getBookById(id: String): Flow<Book?> {
        return dao.getBookWithGenresById(id).map { it?.toBook() }
    }

    override suspend fun refreshBooks(): Result<Unit> {
        return runCatching {
            val bookDtos = api.getBooks()
            bookDtos.forEach { saveBookDtoToDb(it) }
        }
    }

    override suspend fun refreshBookById(id: String): Result<Unit> {
        return runCatching {
            val bookDto = api.getBookById(id)
            saveBookDtoToDb(bookDto)
        }
    }

    override suspend fun addBook(bookCreationPayload: BookCreationPayload): Result<String> {
        return runCatching {
            val bookDetailsCreationDto = BookDetailsCreationDto(
                title = bookCreationPayload.title,
                author = bookCreationPayload.author,
                status = bookCreationPayload.status.value,
                genreIds = bookCreationPayload.genreIds
            )
            val bookDto = api.addBook(bookDetailsCreationDto, bookCreationPayload.coverFile)
            saveBookDtoToDb(bookDto)
            bookDto.id
        }
    }

    override suspend fun updateBook(bookUpdatePayload: BookUpdatePayload): Result<Unit> {
        return runCatching {
            val bookDetailsUpdateDto = BookDetailsUpdateDto(
                title = bookUpdatePayload.title,
                author = bookUpdatePayload.author,
                status = bookUpdatePayload.status.value,
                genreIds = bookUpdatePayload.genreIds
            )
            val bookDto = api.updateBook(
                bookUpdatePayload.id,
                bookDetailsUpdateDto,
                bookUpdatePayload.coverFile
            )
            saveBookDtoToDb(bookDto)
        }
    }

    override suspend fun deleteBook(id: String): Result<Unit> {
        return runCatching {
            api.deleteBook(id)
            dao.deleteBookById(id)
        }
    }

    private suspend fun saveBookDtoToDb(bookDto: BookDto) {
        val bookEntity = bookDto.toBookEntity()
        val bookWithGenres = bookDto.genres.map { genreDto ->
            BookGenresEntity(bookId = bookDto.id, genreId = genreDto.id)
        }
        dao.upsertBookWithGenres(bookEntity, bookWithGenres)
    }
}