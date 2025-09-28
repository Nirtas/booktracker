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

package domain.usecases.book

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.codes.ValidationErrorCode
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.book.UpdateBookDetailsData
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.usecases.book.UpdateBookDetailsUseCase
import ru.jerael.booktracker.backend.domain.validation.GenreValidator
import java.time.Instant
import java.util.*

class UpdateBookDetailsUseCaseTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @MockK
    private lateinit var genreValidator: GenreValidator

    private lateinit var useCase: UpdateBookDetailsUseCase

    private val language = "en"
    private val userId = UUID.randomUUID()
    private val bookId = UUID.randomUUID()
    private val foundGenres = listOf(
        Genre(1, "genre 1"),
        Genre(2, "genre 2"),
        Genre(3, "genre 3")
    )
    private val existingBook = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverUrl = null,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )

    private fun createPayload(genreIds: List<Int> = emptyList()) = BookDetailsUpdatePayload(
        userId = userId,
        bookId = bookId,
        language = language,
        title = "Title",
        author = "Author",
        status = BookStatus.READ,
        genreIds = genreIds
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = UpdateBookDetailsUseCase(bookRepository, genreValidator)
    }

    @Test
    fun `when genre validation is passed, the book must be successfully updated`() = runTest {
        val requestedGenreIds = listOf(1, 2, 3)
        val bookDetailsUpdatePayload = createPayload(requestedGenreIds)
        val updatedBook = existingBook.copy(genres = foundGenres)
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns existingBook
        coEvery { genreValidator.invoke(requestedGenreIds, language) } just Runs
        val updateBookDetailsData = UpdateBookDetailsData(
            userId = userId,
            bookId = bookId,
            title = bookDetailsUpdatePayload.title,
            author = bookDetailsUpdatePayload.author,
            status = bookDetailsUpdatePayload.status,
            genreIds = bookDetailsUpdatePayload.genreIds
        )
        coEvery { bookRepository.updateBookDetails(updateBookDetailsData, language) } returns updatedBook

        val result = useCase.invoke(bookDetailsUpdatePayload)

        assertEquals(updatedBook, result)
        coVerify(exactly = 1) { bookRepository.updateBookDetails(updateBookDetailsData, language) }
    }

    @Test
    fun `when genre validation is failed, a ValidationException should be thrown`() = runTest {
        val requestedGenreIds = listOf(1, 2, 3)
        val bookDetailsUpdatePayload = createPayload(requestedGenreIds)
        val mockkCode = mockk<ValidationErrorCode>()
        val errors = mapOf("genreIds" to listOf(ValidationError(mockkCode)))
        val exception = ValidationException(errors)
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns existingBook
        coEvery { genreValidator.invoke(requestedGenreIds, language) } throws exception

        assertThrows<ValidationException> {
            useCase.invoke(bookDetailsUpdatePayload)
        }

        coVerify(exactly = 0) { bookRepository.updateBookDetails(any(), any()) }
    }

    @Test
    fun `when a book is not found, a BookNotFoundException should be thrown`() = runTest {
        val bookDetailsUpdatePayload = createPayload()
        coEvery { bookRepository.getBookById(userId, bookId, language) } throws BookNotFoundException(bookId.toString())

        assertThrows<BookNotFoundException> {
            useCase.invoke(bookDetailsUpdatePayload)
        }

        coVerify(exactly = 0) { bookRepository.updateBookDetails(any(), any()) }
    }

    @Test
    fun `when repository fails to update book, it should propagate the exception`() = runTest {
        val bookDetailsUpdatePayload = createPayload()
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns existingBook
        coEvery { genreValidator.invoke(any(), any()) } just Runs
        val updateBookDetailsData = UpdateBookDetailsData(
            userId = userId,
            bookId = bookId,
            title = bookDetailsUpdatePayload.title,
            author = bookDetailsUpdatePayload.author,
            status = bookDetailsUpdatePayload.status,
            genreIds = bookDetailsUpdatePayload.genreIds
        )
        coEvery { bookRepository.updateBookDetails(updateBookDetailsData, language) } throws BookNotFoundException(
            bookId.toString()
        )

        val exception = assertThrows<BookNotFoundException> {
            useCase.invoke(bookDetailsUpdatePayload)
        }

        assertTrue(exception.message!!.contains("$bookId"))
    }
}