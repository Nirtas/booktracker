package ru.jerael.booktracker.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.jerael.booktracker.android.data.local.dao.BookDao
import ru.jerael.booktracker.android.data.local.entity.BookGenresEntity
import ru.jerael.booktracker.android.data.mappers.toBook
import ru.jerael.booktracker.android.data.mappers.toBookEntity
import ru.jerael.booktracker.android.data.remote.api.BookApiService
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsCreationDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsUpdateDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDto
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.appFailure
import ru.jerael.booktracker.android.domain.model.appSuccess
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.android.domain.model.book.BookUpdatePayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val dao: BookDao,
    private val api: BookApiService,
    private val errorMapper: ErrorMapper
) : BookRepository {
    override fun getBooks(): Flow<Result<List<Book>>> {
        return dao.getBookWithGenres()
            .map { entities -> appSuccess(entities.map { it.toBook() }) }
            .catch { emit(appFailure(it, errorMapper)) }
    }

    override fun getBookById(id: String): Flow<Result<Book>> {
        return dao.getBookWithGenresById(id)
            .map {
                if (it != null) {
                    appSuccess(it.toBook())
                } else {
                    appFailure(AppError.NotFoundError)
                }
            }
            .catch { emit(appFailure(it, errorMapper)) }
    }

    override suspend fun refreshBooks(): Result<Unit> {
        return try {
            val bookDtos = api.getBooks()
            val booksToInsert = bookDtos.map { it.toBookEntity() }
            val genresToInsert = bookDtos.flatMap { bookDto ->
                bookDto.genres.map { genreDto ->
                    BookGenresEntity(bookId = bookDto.id, genreId = genreDto.id)
                }
            }
            dao.clearAndInsertBooks(booksToInsert, genresToInsert)
            appSuccess(Unit)
        } catch (e: Exception) {
            appFailure(e, errorMapper)
        }
    }

    override suspend fun refreshBookById(id: String): Result<Unit> {
        return try {
            val bookDto = api.getBookById(id)
            saveBookDtoToDb(bookDto)
            appSuccess(Unit)
        } catch (e: Exception) {
            appFailure(e, errorMapper)
        }
    }

    override suspend fun addBook(bookCreationPayload: BookCreationPayload): Result<String> {
        return try {
            val bookDetailsCreationDto = BookDetailsCreationDto(
                title = bookCreationPayload.title,
                author = bookCreationPayload.author,
                status = bookCreationPayload.status.value,
                genreIds = bookCreationPayload.genreIds
            )
            val bookDto = api.addBook(bookDetailsCreationDto, bookCreationPayload.coverFile)
            saveBookDtoToDb(bookDto)
            appSuccess(bookDto.id)
        } catch (e: Exception) {
            appFailure(e, errorMapper)
        }
    }

    override suspend fun updateBook(bookUpdatePayload: BookUpdatePayload): Result<Unit> {
        return try {
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
            appSuccess(Unit)
        } catch (e: Exception) {
            appFailure(e, errorMapper)
        }
    }

    override suspend fun deleteBook(id: String): Result<Unit> {
        return try {
            api.deleteBook(id)
            dao.deleteBookById(id)
            appSuccess(Unit)
        } catch (e: Exception) {
            appFailure(e, errorMapper)
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