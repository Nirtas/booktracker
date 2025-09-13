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

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.usecases.book.GetBookByIdUseCase
import java.time.Instant
import java.util.*

class GetBookByIdUseCaseTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    private lateinit var useCase: GetBookByIdUseCase

    private val language = "en"
    private val bookId = UUID.randomUUID()
    private val expectedBook = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverPath = null,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetBookByIdUseCase(bookRepository)
    }

    @Test
    fun `when a book is found, the book is successfully returned`() = runTest {
        coEvery { bookRepository.getBookById(bookId, language) } returns expectedBook

        val result = useCase.invoke(bookId, language)

        assertEquals(expectedBook, result)
    }

    @Test
    fun `when a book is not found, a BookNotFoundException should be thrown`() = runTest {
        coEvery { bookRepository.getBookById(bookId, language) } returns null

        val exception = assertThrows<BookNotFoundException> {
            useCase.invoke(bookId, language)
        }

        assertTrue(exception.message!!.contains("$bookId"))
    }
}