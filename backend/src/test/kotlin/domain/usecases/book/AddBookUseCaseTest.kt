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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.usecases.book.AddBookUseCase
import ru.jerael.booktracker.backend.domain.validation.GenreValidator
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class AddBookUseCaseTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @MockK
    private lateinit var genreValidator: GenreValidator

    @MockK
    private lateinit var coverStorage: CoverStorage

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
        coverPath = null,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )

    private fun createPayload(genreIds: List<Int> = emptyList()) = BookCreationPayload(
        title = "Title",
        author = "Author",
        coverPath = null,
        status = BookStatus.READ,
        genreIds = genreIds
    )

    private val coverBytes: ByteArray = "file content".toByteArray()
    private val coverFileName: String = "cover.jpg"

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = AddBookUseCase(bookRepository, genreValidator, coverStorage)
    }

    @Test
    fun `when genre validation is passed and the cover is present, the book must be successfully created with the cover`() =
        runTest {
            val requestedGenreIds = listOf(1, 2, 3)
            val bookCreationPayload = createPayload(requestedGenreIds)
            val expectedBookWithGenres = expectedBook.copy(genres = foundGenres)
            val expectedCoverPath = "covers/new_book.jpg"
            coEvery { genreValidator.invoke(requestedGenreIds, language) } just Runs
            coEvery { coverStorage.save(coverBytes, coverFileName) } returns expectedCoverPath
            coEvery { bookRepository.addBook(any(), language) } returns expectedBookWithGenres

            val result = useCase.invoke(bookCreationPayload, coverBytes, coverFileName, language)

            assertEquals(expectedBookWithGenres, result)
            coVerify(exactly = 1) { coverStorage.save(coverBytes, coverFileName) }
            coVerify(exactly = 1) { bookRepository.addBook(any(), language) }
        }

    @Test
    fun `when genre validation is passed and the cover is not present, the book must be successfully created without cover`() =
        runTest {
            val requestedGenreIds = listOf(1, 2, 3)
            val bookCreationPayload = createPayload(requestedGenreIds)
            val expectedBookWithGenres = expectedBook.copy(genres = foundGenres)
            coEvery { genreValidator.invoke(requestedGenreIds, language) } just Runs
            coEvery { bookRepository.addBook(bookCreationPayload, language) } returns expectedBookWithGenres

            val result = useCase.invoke(bookCreationPayload, null, null, language)

            assertEquals(expectedBookWithGenres, result)
            coVerify(exactly = 0) { coverStorage.save(any(), any()) }
            coVerify(exactly = 1) { bookRepository.addBook(any(), language) }
        }

    @Test
    fun `when genre validation is failed, a ValidationException should be thrown`() = runTest {
        val requestedGenreIds = listOf(1, 2, 3)
        val bookCreationPayload = createPayload(requestedGenreIds)
        coEvery { genreValidator.invoke(requestedGenreIds, language) } throws ValidationException("Error")

        assertThrows<ValidationException> {
            useCase.invoke(bookCreationPayload, coverBytes, coverFileName, language)
        }

        coVerify(exactly = 0) { coverStorage.save(any(), any()) }
        coVerify(exactly = 0) { bookRepository.addBook(any(), any()) }
    }
}