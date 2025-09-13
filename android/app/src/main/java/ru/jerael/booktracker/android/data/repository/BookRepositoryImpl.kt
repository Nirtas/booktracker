/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.jerael.booktracker.android.data.local.dao.BookDao
import ru.jerael.booktracker.android.data.local.entity.BookGenresEntity
import ru.jerael.booktracker.android.data.mappers.BookMapper
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

class BookRepositoryImpl @Inject constructor(
    private val dao: BookDao,
    private val api: BookApiService,
    private val errorMapper: ErrorMapper,
    private val bookMapper: BookMapper
) : BookRepository {
    override fun getBooks(): Flow<Result<List<Book>>> {
        return dao.getBooksWithGenres()
            .map { booksWithGenres ->
                appSuccess(bookMapper.mapBooksWithGenresToBooks(booksWithGenres))
            }
            .catch { emit(appFailure(it, errorMapper)) }
    }

    override fun getBookById(id: String): Flow<Result<Book>> {
        return dao.getBookWithGenresById(id)
            .map {
                if (it != null) {
                    appSuccess(bookMapper.mapBookWithGenresToBook(it))
                } else {
                    appFailure(AppError.NotFoundError)
                }
            }
            .catch { emit(appFailure(it, errorMapper)) }
    }

    override suspend fun refreshBooks(): Result<Unit> {
        return try {
            val bookDtos = api.getBooks()
            val booksToInsert = bookMapper.mapDtosToEntities(bookDtos)
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
            var bookDto = api.updateBookDetails(bookUpdatePayload.id, bookDetailsUpdateDto)
            bookUpdatePayload.coverFile?.let { file ->
                bookDto = api.updateBookCover(bookUpdatePayload.id, bookUpdatePayload.coverFile)
            }
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
        val bookEntity = bookMapper.mapDtoToEntity(bookDto)
        val bookWithGenres = bookDto.genres.map { genreDto ->
            BookGenresEntity(bookId = bookDto.id, genreId = genreDto.id)
        }
        dao.upsertBookWithGenres(bookEntity, bookWithGenres)
    }
}