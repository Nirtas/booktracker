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
import org.junit.jupiter.api.*
import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.codes.ValidationErrorCode
import ru.jerael.booktracker.backend.domain.model.book.AddBookData
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.usecases.book.AddBookUseCase
import ru.jerael.booktracker.backend.domain.validation.CoverValidator
import ru.jerael.booktracker.backend.domain.validation.GenreValidator
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddBookUseCaseTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @MockK
    private lateinit var genreValidator: GenreValidator

    @MockK
    private lateinit var coverStorage: CoverStorage

    private val coverValidator: CoverValidator = CoverValidator()

    private lateinit var useCase: AddBookUseCase

    private val language = "en"
    private val bookId = UUID.randomUUID()
    private val foundGenres = listOf(
        Genre(1, "genre 1"),
        Genre(2, "genre 2"),
        Genre(3, "genre 3")
    )
    private val expectedBook = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverUrl = null,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )

    private val coverBytes: ByteArray = "file content".toByteArray()
    private val coverFileName: String = "cover.jpg"
    private val imageBaseUrl = "http://example.com"

    private val userId = UUID.randomUUID()

    private fun createPayload(
        genreIds: List<Int> = emptyList(),
        coverBytes: ByteArray? = null,
        coverFileName: String? = null
    ) = BookCreationPayload(
        userId = userId,
        language = "en",
        title = "Title",
        author = "Author",
        coverBytes = coverBytes,
        coverFileName = coverFileName,
        status = BookStatus.READ,
        genreIds = genreIds
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = AddBookUseCase(bookRepository, genreValidator, coverStorage, coverValidator)
    }

    @Test
    fun `when genre validation is passed and the cover is present, the book must be successfully created with the cover`() =
        runTest {
            val requestedGenreIds = listOf(1, 2, 3)
            val bookCreationPayload = createPayload(requestedGenreIds, coverBytes, coverFileName)
            val expectedBookWithGenres = expectedBook.copy(genres = foundGenres)
            val pathSlot = slot<String>()
            val addBookDataSlot = slot<AddBookData>()
            coEvery { genreValidator.invoke(requestedGenreIds, language) } just Runs
            coEvery { coverStorage.save(capture(pathSlot), coverBytes) } answers {
                "$imageBaseUrl/${pathSlot.captured}"
            }
            coEvery { bookRepository.addBook(capture(addBookDataSlot), language) } returns expectedBookWithGenres

            val result = useCase.invoke(bookCreationPayload)

            assertEquals(expectedBookWithGenres, result)
            coVerify(exactly = 1) { coverStorage.save(any(), coverBytes) }
            coVerify(exactly = 1) { bookRepository.addBook(any(), language) }

            val capturedPath = pathSlot.captured
            assertTrue(capturedPath.startsWith("$userId/covers/"))
            assertTrue(capturedPath.endsWith(".jpg"))
            val capturedAddBookData = addBookDataSlot.captured
            assertNotNull(capturedAddBookData.coverUrl)
            assertEquals("$imageBaseUrl/$capturedPath", capturedAddBookData.coverUrl)
        }

    @Test
    fun `when genre validation is passed and the cover is not present, the book must be successfully created without cover`() =
        runTest {
            val requestedGenreIds = listOf(1, 2, 3)
            val bookCreationPayload = createPayload(requestedGenreIds)
            val expectedBookWithGenres = expectedBook.copy(genres = foundGenres)
            val addBookDataSlot = slot<AddBookData>()
            coEvery { genreValidator.invoke(requestedGenreIds, language) } just Runs
            coEvery { bookRepository.addBook(capture(addBookDataSlot), language) } returns expectedBookWithGenres

            val result = useCase.invoke(bookCreationPayload)

            assertEquals(expectedBookWithGenres, result)
            coVerify(exactly = 0) { coverStorage.save(any(), any()) }
            coVerify(exactly = 1) { bookRepository.addBook(any(), language) }

            val capturedAddBookData = addBookDataSlot.captured
            assertNull(capturedAddBookData.coverUrl)
        }

    @Test
    fun `when genre validation is failed, a ValidationException should be thrown`() = runTest {
        val requestedGenreIds = listOf(1, 2, 3)
        val bookCreationPayload = createPayload(requestedGenreIds)
        val mockkCode = mockk<ValidationErrorCode>()
        val errors = mapOf("genreIds" to listOf(ValidationError(mockkCode)))
        val exception = ValidationException(errors)
        coEvery { genreValidator.invoke(requestedGenreIds, language) } throws exception

        assertThrows<ValidationException> {
            useCase.invoke(bookCreationPayload)
        }

        coVerify(exactly = 0) { coverStorage.save(any(), any()) }
        coVerify(exactly = 0) { bookRepository.addBook(any(), any()) }
    }
}