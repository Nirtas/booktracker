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

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.android.data.local.dao.BookDao
import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.data.local.entity.BookGenresEntity
import ru.jerael.booktracker.android.data.local.relations.BookWithGenres
import ru.jerael.booktracker.android.data.mappers.BookMapper
import ru.jerael.booktracker.android.data.remote.api.BookApiService
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsCreationDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsUpdateDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDto
import ru.jerael.booktracker.android.data.remote.dto.genre.GenreDto
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.book.BookUpdatePayload
import ru.jerael.booktracker.android.domain.model.exceptions.AppException
import ru.jerael.booktracker.android.domain.repository.BookRepository
import java.io.File
import java.time.Instant

class BookRepositoryImplTest {

    @MockK
    private lateinit var dao: BookDao

    @MockK
    private lateinit var api: BookApiService

    @MockK
    private lateinit var errorMapper: ErrorMapper

    @MockK
    private lateinit var bookMapper: BookMapper

    private lateinit var repository: BookRepository

    private val bookId = "e607ac1d-309f-4301-b71f-52b59e7cb4db"
    private val bookDto = BookDto(
        id = bookId,
        title = "Title",
        author = "Author",
        coverUrl = null,
        status = BookStatus.WANT_TO_READ.value,
        createdAt = Instant.now().toEpochMilli(),
        genres = listOf(
            GenreDto(id = 1, name = "gaming"),
            GenreDto(id = 2, name = "adventure"),
            GenreDto(id = 3, name = "science fiction")
        )
    )

    private val bookUpdatePayload = BookUpdatePayload(
        id = bookId,
        title = bookDto.title,
        author = bookDto.author,
        coverFile = null,
        status = BookStatus.fromString(bookDto.status)!!,
        genreIds = bookDto.genres.map { it.id }
    )

    private val bookDetailsUpdateDto = BookDetailsUpdateDto(
        title = bookUpdatePayload.title,
        author = bookUpdatePayload.author,
        status = bookUpdatePayload.status.value,
        genreIds = bookUpdatePayload.genreIds
    )

    private val bookCreationPayload = BookCreationPayload(
        title = bookDto.title,
        author = bookDto.author,
        coverFile = null,
        status = BookStatus.fromString(bookDto.status)!!,
        genreIds = bookDto.genres.map { it.id }
    )

    private val bookDetailsCreationDto = BookDetailsCreationDto(
        title = bookCreationPayload.title,
        author = bookCreationPayload.author,
        status = bookCreationPayload.status.value,
        genreIds = bookCreationPayload.genreIds
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        repository = BookRepositoryImpl(dao, api, errorMapper, bookMapper)
    }

    @Test
    fun `when dao returns data, getBooks should emit success with mapped books`() = runTest {
        val books: List<BookWithGenres> = listOf(mockk(), mockk())
        val mappedBooks: List<Book> = listOf(mockk(), mockk())
        every { dao.getBooksWithGenres() } returns flowOf(books)
        every { bookMapper.mapBooksWithGenresToBooks(books) } returns mappedBooks

        repository.getBooks().test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(mappedBooks, result.getOrNull())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when dao flow emits an error, getBooks should emit failure`() = runTest {
        val exception = RuntimeException("Error")
        val mappedError = AppError.UnknownError
        every { dao.getBooksWithGenres() } returns flow { throw exception }
        coEvery { errorMapper.map(exception) } returns mappedError

        repository.getBooks().test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            val actualException = result.exceptionOrNull()
            assertTrue(actualException is AppException)
            assertEquals(mappedError, (actualException as AppException).appError)
            cancelAndConsumeRemainingEvents()
        }

        verify(exactly = 0) { bookMapper.mapBooksWithGenresToBooks(any()) }
    }

    @Test
    fun `when dao finds a book, getBookById should emit success with the mapped book`() = runTest {
        val book: BookWithGenres = mockk()
        val mappedBook: Book = mockk()
        every { dao.getBookWithGenresById(bookId) } returns flowOf(book)
        every { bookMapper.mapBookWithGenresToBook(book) } returns mappedBook

        repository.getBookById(bookId).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(mappedBook, result.getOrNull())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when dao returns null, getBookById should emit failure with NotFoundError`() = runTest {
        every { dao.getBookWithGenresById(bookId) } returns flowOf(null)

        repository.getBookById(bookId).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue(exception is AppException)
            assertEquals(AppError.NotFoundError, (exception as AppException).appError)
            cancelAndConsumeRemainingEvents()
        }

        verify(exactly = 0) { bookMapper.mapBookWithGenresToBook(any()) }
    }

    @Test
    fun `when dao flow emits an error, getBookById should emit failure`() = runTest {
        val exception = RuntimeException("Error")
        val mappedError = AppError.UnknownError
        every { dao.getBookWithGenresById(bookId) } returns flow { throw exception }
        coEvery { errorMapper.map(exception) } returns mappedError

        repository.getBookById(bookId).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            val actualException = result.exceptionOrNull()
            assertTrue(actualException is AppException)
            assertEquals(mappedError, (actualException as AppException).appError)
            cancelAndConsumeRemainingEvents()
        }

        verify(exactly = 0) { bookMapper.mapBookWithGenresToBook(any()) }
    }

    @Test
    fun `when api returns books successfully, refreshBooks should save them to dao and return success`() =
        runTest {
            val books: List<BookDto> = listOf(bookDto)
            val mappedBooks: List<BookEntity> = listOf(mockk())
            val expectedGenres = books.flatMap { bookDto ->
                bookDto.genres.map { genreDto ->
                    BookGenresEntity(bookDto.id, genreDto.id)
                }
            }
            coEvery { api.getBooks() } returns books
            every { bookMapper.mapDtosToEntities(books) } returns mappedBooks
            coEvery { dao.clearAndInsertBooks(mappedBooks, expectedGenres) } just Runs

            val result = repository.refreshBooks()

            assertTrue(result.isSuccess)
            assertEquals(Unit, result.getOrNull())
        }

    @Test
    fun `when api throws an exception, refreshBooks should not access dao and return failure`() =
        runTest {
            val exception = RuntimeException("Error")
            val mappedError = AppError.UnknownError
            coEvery { api.getBooks() } throws exception
            coEvery { errorMapper.map(exception) } returns mappedError

            val result = repository.refreshBooks()

            assertTrue(result.isFailure)
            val actualException = result.exceptionOrNull()
            assertTrue(actualException is AppException)
            assertEquals(mappedError, (actualException as AppException).appError)
            coVerify(exactly = 0) { bookMapper.mapDtosToEntities(any()) }
            coVerify(exactly = 0) { dao.clearAndInsertBooks(any(), any()) }
        }

    @Test
    fun `when api returns a book successfully, refreshBookById should save it to dao and return success`() =
        runTest {
            val mappedBook: BookEntity = mockk()
            val expectedGenres = bookDto.genres.map { genreDto ->
                BookGenresEntity(bookId = bookDto.id, genreId = genreDto.id)
            }
            coEvery { api.getBookById(bookId) } returns bookDto
            every { bookMapper.mapDtoToEntity(bookDto) } returns mappedBook
            coEvery { dao.upsertBookWithGenres(mappedBook, expectedGenres) } just Runs

            val result = repository.refreshBookById(bookId)

            assertTrue(result.isSuccess)
        }

    @Test
    fun `when api throws an exception, refreshBookById should not access dao and return failure`() =
        runTest {
            val exception = RuntimeException("Error")
            val mappedError = AppError.UnknownError
            coEvery { api.getBookById(bookId) } throws exception
            coEvery { errorMapper.map(exception) } returns mappedError

            val result = repository.refreshBookById(bookId)

            assertTrue(result.isFailure)
            val actualException = result.exceptionOrNull()
            assertTrue(actualException is AppException)
            assertEquals(mappedError, (actualException as AppException).appError)
            coVerify(exactly = 0) { bookMapper.mapDtoToEntity(any()) }
            coVerify(exactly = 0) { dao.upsertBookWithGenres(any(), any()) }
        }

    @Test
    fun `when api creates a book successfully, addBook should save it to dao and return success with new id`() =
        runTest {
            val mappedBook: BookEntity = mockk()
            val expectedGenres = bookDto.genres.map { genreDto ->
                BookGenresEntity(bookId = bookDto.id, genreId = genreDto.id)
            }
            coEvery { api.addBook(bookDetailsCreationDto, null) } returns bookDto
            every { bookMapper.mapDtoToEntity(bookDto) } returns mappedBook
            coEvery { dao.upsertBookWithGenres(mappedBook, expectedGenres) } just Runs

            val result = repository.addBook(bookCreationPayload)

            assertTrue(result.isSuccess)
            assertEquals(bookId, result.getOrNull())
        }

    @Test
    fun `when api throws an exception, addBook should not access dao and return failure`() =
        runTest {
            val exception = RuntimeException("Error")
            val mappedError = AppError.UnknownError
            coEvery { api.addBook(any(), any()) } throws exception
            coEvery { errorMapper.map(exception) } returns mappedError

            val result = repository.addBook(mockk(relaxed = true))

            assertTrue(result.isFailure)
            val actualException = result.exceptionOrNull()
            assertTrue(actualException is AppException)
            assertEquals(mappedError, (actualException as AppException).appError)
            coVerify(exactly = 0) { bookMapper.mapDtoToEntity(any()) }
            coVerify(exactly = 0) { dao.upsertBookWithGenres(any(), any()) }
        }

    @Test
    fun `when updating details and cover, updateBook should call both api update methods and save to dao`() =
        runTest {
            val coverFile = mockk<File>()
            val payload = bookUpdatePayload.copy(coverFile = coverFile)
            val mappedBook: BookEntity = mockk()
            val expectedGenres = bookDto.genres.map { genreDto ->
                BookGenresEntity(bookId = bookDto.id, genreId = genreDto.id)
            }
            coEvery {
                api.updateBookDetails(
                    bookId,
                    bookDetailsUpdateDto
                )
            } returns bookDto
            coEvery { api.updateBookCover(payload.id, coverFile) } returns bookDto
            every { bookMapper.mapDtoToEntity(bookDto) } returns mappedBook
            coEvery { dao.upsertBookWithGenres(mappedBook, expectedGenres) } just Runs

            val result = repository.updateBook(payload)

            assertTrue(result.isSuccess)
            coVerifyOrder {
                api.updateBookDetails(any(), any())
                api.updateBookCover(any(), any())
            }
        }

    @Test
    fun `when updating details only, updateBook should call api details update and save to dao`() =
        runTest {
            val mappedBook: BookEntity = mockk()
            val expectedGenres = bookDto.genres.map { genreDto ->
                BookGenresEntity(bookId = bookDto.id, genreId = genreDto.id)
            }
            coEvery {
                api.updateBookDetails(
                    bookId,
                    bookDetailsUpdateDto
                )
            } returns bookDto
            every { bookMapper.mapDtoToEntity(bookDto) } returns mappedBook
            coEvery { dao.upsertBookWithGenres(mappedBook, expectedGenres) } just Runs

            val result = repository.updateBook(bookUpdatePayload)

            assertTrue(result.isSuccess)
            coVerify(exactly = 0) { api.updateBookCover(any(), any()) }
        }

    @Test
    fun `when api details update fails, updateBook should not call cover update or save to dao and return failure`() =
        runTest {
            val exception = RuntimeException("Error")
            val mappedError = AppError.UnknownError
            coEvery { api.updateBookDetails(any(), any()) } throws exception
            coEvery { errorMapper.map(exception) } returns mappedError

            val result = repository.updateBook(mockk(relaxed = true))

            assertTrue(result.isFailure)
            val actualException = result.exceptionOrNull()
            assertTrue(actualException is AppException)
            assertEquals(mappedError, (actualException as AppException).appError)
            coVerify(exactly = 0) { api.updateBookCover(any(), any()) }
            coVerify(exactly = 0) { bookMapper.mapDtoToEntity(any()) }
            coVerify(exactly = 0) { dao.upsertBookWithGenres(any(), any()) }
        }

    @Test
    fun `when api cover update fails, updateBook should not save to dao and return failure`() =
        runTest {
            val coverFile = mockk<File>()
            val payload = bookUpdatePayload.copy(coverFile = coverFile)
            val exception = RuntimeException("Error")
            val mappedError = AppError.UnknownError
            coEvery {
                api.updateBookDetails(
                    bookId,
                    bookDetailsUpdateDto
                )
            } returns bookDto
            coEvery { api.updateBookCover(any(), any()) } throws exception
            coEvery { errorMapper.map(exception) } returns mappedError

            val result = repository.updateBook(payload)

            assertTrue(result.isFailure)
            val actualException = result.exceptionOrNull()
            assertTrue(actualException is AppException)
            assertEquals(mappedError, (actualException as AppException).appError)
            coVerify(exactly = 1) { api.updateBookDetails(any(), any()) }
            coVerify(exactly = 0) { bookMapper.mapDtoToEntity(any()) }
            coVerify(exactly = 0) { dao.upsertBookWithGenres(any(), any()) }
        }

    @Test
    fun `when api deletes a book successfully, deleteBook should delete from dao and return success`() =
        runTest {
            coEvery { api.deleteBook(bookId) } just Runs
            coEvery { dao.deleteBookById(bookId) } just Runs

            val result = repository.deleteBook(bookId)

            assertTrue(result.isSuccess)
            coVerify {
                api.deleteBook(any())
                dao.deleteBookById(any())
            }
        }

    @Test
    fun `when api throws an exception, deleteBook should not access dao and return failure`() =
        runTest {
            val exception = RuntimeException("Error")
            val mappedError = AppError.UnknownError
            coEvery { api.deleteBook(any()) } throws exception
            coEvery { errorMapper.map(exception) } returns mappedError

            val result = repository.deleteBook(bookId)

            assertTrue(result.isFailure)
            val actualException = result.exceptionOrNull()
            assertTrue(actualException is AppException)
            assertEquals(mappedError, (actualException as AppException).appError)
            coVerify(exactly = 0) { dao.deleteBookById(any()) }
        }

    @Test
    fun `when dao throws an exception, deleteBook should return failure`() = runTest {
        val exception = RuntimeException("Error")
        val mappedError = AppError.UnknownError
        coEvery { api.deleteBook(any()) } just Runs
        coEvery { dao.deleteBookById(any()) } throws exception
        coEvery { errorMapper.map(exception) } returns mappedError

        val result = repository.deleteBook(bookId)

        assertTrue(result.isFailure)
        val actualException = result.exceptionOrNull()
        assertTrue(actualException is AppException)
        assertEquals(mappedError, (actualException as AppException).appError)
    }
}