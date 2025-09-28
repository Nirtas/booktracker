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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.exceptions.StorageException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCoverUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.book.UpdateBookCoverData
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.usecases.book.UpdateBookCoverUseCase
import ru.jerael.booktracker.backend.domain.validation.CoverValidator
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class UpdateBookCoverUseCaseTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @MockK
    private lateinit var coverStorage: CoverStorage

    private val coverValidator: CoverValidator = CoverValidator()

    private lateinit var useCase: UpdateBookCoverUseCase

    private val coverBytes: ByteArray = "file content".toByteArray()
    private val coverFileName: String = "cover.jpg"

    private val language = "en"
    private val userId = UUID.randomUUID()
    private val bookId = UUID.randomUUID()
    private val oldCoverPath = "$userId/covers/old_book.jpg"
    private val imageBaseUrl = "http://example.com"
    private val oldCoverUrl = "$imageBaseUrl/$oldCoverPath"
    private val existingBookWithCover = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverUrl = oldCoverUrl,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )
    private val existingBookWithoutCover = existingBookWithCover.copy(coverUrl = null)
    private val newCoverPath = "$userId/covers/new_book.jpg"
    private val newCoverUrl = "$imageBaseUrl/$newCoverPath"
    private val updatedBook = existingBookWithCover.copy(coverUrl = newCoverUrl)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = UpdateBookCoverUseCase(bookRepository, coverStorage, coverValidator)
    }

    @Test
    fun `when everything is fine, the cover is successfully updated`() = runTest {
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns existingBookWithCover
        coEvery { coverStorage.delete(oldCoverUrl) } just Runs
        coEvery { coverStorage.save(any(), coverBytes) } returns newCoverUrl
        val updateBookCoverData = UpdateBookCoverData(
            userId = userId,
            bookId = bookId,
            coverUrl = newCoverUrl
        )
        coEvery { bookRepository.updateBookCover(updateBookCoverData, language) } returns updatedBook
        val bookCoverUpdatePayload = BookCoverUpdatePayload(
            userId = userId,
            bookId = bookId,
            language = language,
            coverBytes = coverBytes,
            coverFileName = coverFileName
        )

        val result = useCase.invoke(bookCoverUpdatePayload)

        assertEquals(updatedBook, result)
        coVerify(exactly = 1) { bookRepository.getBookById(userId, bookId, language) }
        coVerify(exactly = 1) { coverStorage.delete(oldCoverUrl) }
        coVerify(exactly = 1) { coverStorage.save(any(), coverBytes) }
        coVerify(exactly = 1) { bookRepository.updateBookCover(updateBookCoverData, language) }
    }

    @Test
    fun `when a book is not found, a BookNotFoundException should be thrown`() = runTest {
        coEvery { bookRepository.getBookById(userId, bookId, language) } throws BookNotFoundException(bookId.toString())
        val bookCoverUpdatePayload = BookCoverUpdatePayload(
            userId = userId,
            bookId = bookId,
            language = language,
            coverBytes = coverBytes,
            coverFileName = coverFileName
        )

        assertThrows<BookNotFoundException> {
            useCase.invoke(bookCoverUpdatePayload)
        }

        coVerify(exactly = 0) { coverStorage.delete(any()) }
        coVerify(exactly = 0) { coverStorage.save(any(), any()) }
        coVerify(exactly = 0) { bookRepository.updateBookCover(any(), any()) }
    }

    @Test
    fun `when it is not possible to remove a cover from storage, a StorageException should be thrown`() = runTest {
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns existingBookWithCover
        coEvery { coverStorage.delete(oldCoverUrl) } throws StorageException(message = "Error")
        val bookCoverUpdatePayload = BookCoverUpdatePayload(
            userId = userId,
            bookId = bookId,
            language = language,
            coverBytes = coverBytes,
            coverFileName = coverFileName
        )

        assertThrows<StorageException> {
            useCase.invoke(bookCoverUpdatePayload)
        }

        coVerify(exactly = 0) { coverStorage.save(any(), any()) }
        coVerify(exactly = 0) { bookRepository.updateBookCover(any(), any()) }
    }

    @Test
    fun `when it is not possible to save a new cover to storage, a StorageException should be thrown`() = runTest {
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns existingBookWithCover
        coEvery { coverStorage.delete(oldCoverUrl) } just Runs
        coEvery { coverStorage.save(any(), coverBytes) } throws StorageException(message = "Error")
        val bookCoverUpdatePayload = BookCoverUpdatePayload(
            userId = userId,
            bookId = bookId,
            language = language,
            coverBytes = coverBytes,
            coverFileName = coverFileName
        )

        assertThrows<StorageException> {
            useCase.invoke(bookCoverUpdatePayload)
        }

        coVerify(exactly = 0) { bookRepository.updateBookCover(any(), any()) }
    }

    @Test
    fun `when repository fails to update book, it should propagate the exception`() = runTest {
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns existingBookWithCover
        coEvery { coverStorage.delete(oldCoverUrl) } just Runs
        coEvery { coverStorage.save(any(), coverBytes) } returns newCoverUrl
        val updateBookCoverData = UpdateBookCoverData(
            userId = userId,
            bookId = bookId,
            coverUrl = newCoverUrl
        )
        coEvery {
            bookRepository.updateBookCover(
                updateBookCoverData,
                language
            )
        } throws BookNotFoundException(bookId.toString())
        val bookCoverUpdatePayload = BookCoverUpdatePayload(
            userId = userId,
            bookId = bookId,
            language = language,
            coverBytes = coverBytes,
            coverFileName = coverFileName
        )

        val exception = assertThrows<BookNotFoundException> {
            useCase.invoke(bookCoverUpdatePayload)
        }

        assertTrue(exception.message!!.contains("$bookId"))
    }

    @Test
    fun `when the book does not have a cover, it is not deleted`() = runTest {
        coEvery { bookRepository.getBookById(userId, bookId, language) } returns existingBookWithoutCover
        coEvery { coverStorage.save(any(), coverBytes) } returns newCoverUrl
        val updateBookCoverData = UpdateBookCoverData(
            userId = userId,
            bookId = bookId,
            coverUrl = newCoverUrl
        )
        coEvery { bookRepository.updateBookCover(updateBookCoverData, language) } returns updatedBook
        val bookCoverUpdatePayload = BookCoverUpdatePayload(
            userId = userId,
            bookId = bookId,
            language = language,
            coverBytes = coverBytes,
            coverFileName = coverFileName
        )

        val result = useCase.invoke(bookCoverUpdatePayload)

        assertEquals(updatedBook, result)
        coVerify(exactly = 0) { coverStorage.delete(any()) }
        coVerify(exactly = 1) { coverStorage.save(any(), coverBytes) }
        coVerify(exactly = 1) { bookRepository.updateBookCover(updateBookCoverData, language) }
    }
}