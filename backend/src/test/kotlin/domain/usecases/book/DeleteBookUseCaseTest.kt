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
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.ExternalServiceException
import ru.jerael.booktracker.backend.domain.exceptions.StorageException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.usecases.book.DeleteBookUseCase
import java.time.Instant
import java.util.*

class DeleteBookUseCaseTest {

    @RelaxedMockK
    private lateinit var bookRepository: BookRepository

    @RelaxedMockK
    private lateinit var coverStorage: CoverStorage

    private lateinit var useCase: DeleteBookUseCase

    private val language = "en"
    private val userId = UUID.randomUUID()
    private val bookId = UUID.randomUUID()
    private val coverPath = "covers/book.jpg"
    private val imageBaseUrl = "http://example.com"
    private val coverUrl = "$imageBaseUrl/$coverPath"
    private val bookWithCover = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverUrl = coverUrl,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )
    private val bookWithoutCover = bookWithCover.copy(coverUrl = null)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = DeleteBookUseCase(bookRepository, coverStorage)
    }

    @Test
    fun `when the book and cover have been successfully removed`() = runTest {
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns bookWithCover
        coEvery { bookRepository.deleteBook(userId, bookId) } just Runs

        useCase.invoke(userId, bookId)

        coVerify(exactly = 1) { bookRepository.getBookById(userId, bookId, language) }
        coVerify(exactly = 1) { coverStorage.delete(coverUrl) }
        coVerify(exactly = 1) { bookRepository.deleteBook(userId, bookId) }
    }

    @Test
    fun `when a book has no cover, only the book details are successfully removed`() = runTest {
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns bookWithoutCover
        coEvery { bookRepository.deleteBook(userId, bookId) } just Runs

        useCase.invoke(userId, bookId)

        coVerify(exactly = 1) { bookRepository.getBookById(userId, bookId, language) }
        coVerify(exactly = 0) { coverStorage.delete(any()) }
        coVerify(exactly = 1) { bookRepository.deleteBook(userId, bookId) }
    }

    @Test
    fun `when it is not possible to remove a cover from storage, a StorageException should be thrown`() = runTest {
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns bookWithCover
        coEvery { coverStorage.delete(any()) } throws StorageException(message = "Error")

        assertThrows<StorageException> {
            useCase.invoke(userId, bookId)
        }

        coVerify(exactly = 0) { bookRepository.deleteBook(userId, bookId) }
    }

    @Test
    fun `when it is not possible to delete book details from the database, an ExternalServiceException should be thrown`() =
        runTest {
            coEvery { bookRepository.getBookById(userId, bookId, language) } returns bookWithCover
            coEvery { bookRepository.deleteBook(any(), any()) } throws ExternalServiceException("Error", "Error")

            assertThrows<ExternalServiceException> {
                useCase.invoke(userId, bookId)
            }

            coVerify(exactly = 1) { coverStorage.delete(coverUrl) }
        }
}